package com.therandomlabs.curseapi;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.forgesvc.ForgeSVCProvider;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.game.CurseGameVersion;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.CurseSearchQuery;
import com.therandomlabs.curseapi.util.CheckedFunction;
import com.therandomlabs.curseapi.util.OkHttpUtils;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main CurseAPI class.
 * <p>
 * Contains methods for retrieving {@link CurseProject} and {@link CurseFile} instances as well
 * as for managing {@link CurseAPIProvider}s.
 */
public final class CurseAPI {
	/**
	 * The minimum CurseForge game ID.
	 */
	public static final int MIN_GAME_ID = 1;

	/**
	 * The minimum CurseForge category section ID.
	 */
	public static final int MIN_CATEGORY_SECTION_ID = 1;

	/**
	 * The minimum CurseForge category ID.
	 */
	public static final int MIN_CATEGORY_ID = 1;

	/**
	 * The minimum CurseForge project ID.
	 */
	public static final int MIN_PROJECT_ID = 10;

	/**
	 * The minimum CurseForge file ID.
	 */
	public static final int MIN_FILE_ID = 60018;

	/**
	 * The placeholder CurseForge project avatar URL.
	 */
	public static final HttpUrl PLACEHOLDER_PROJECT_AVATAR = HttpUrl.get(
			"https://www.curseforge.com/Content/2-0-7263-28137/Skins/Elerium/images/icons/" +
					"avatar-flame.png"
	);

	/**
	 * The placeholder CurseForge project thumbnail URL.
	 */
	public static final HttpUrl PLACEHOLDER_PROJECT_THUMBNAIL =
			HttpUrl.get("https://media.forgecdn.net/avatars/0/93/635227964539626926.png");

	/**
	 * The {@link Element} returned by {@link CurseFile#changelog()} if no changelog
	 * is provided.
	 */
	public static final Element NO_CHANGELOG_PROVIDED =
			new Element("p").appendText("No changelog provided.");

	private static final Logger logger = LoggerFactory.getLogger(CurseAPI.class);

	private static final List<CurseAPIProvider> providers =
			Lists.newArrayList(ForgeSVCProvider.instance);

	private CurseAPI() {}

	/**
	 * Returns a {@link CurseProject} instance for the specified project ID.
	 *
	 * @param id a project ID.
	 * @return a {@link CurseProject} instance for the specified project ID wrapped in an
	 * {@link Optional} if the project exists, or otherwise {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<CurseProject> project(int id) throws CurseException {
		CursePreconditions.checkProjectID(id, "id");
		return get(provider -> provider.project(id));
	}

	/**
	 * Returns the description for the project with the specified ID.
	 *
	 * @param id a project ID.
	 * @return an {@link Element} containing the description for the project with the specified ID
	 * wrapped in an {@link Optional} if the project exists, or otherwise {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<Element> projectDescription(int id) throws CurseException {
		CursePreconditions.checkProjectID(id, "id");
		return get(provider -> provider.projectDescription(id));
	}

	/**
	 * Executes a {@link CurseSearchQuery}.
	 *
	 * @param query a {@link CurseSearchQuery}.
	 * @return a mutable {@link List} of {@link CurseProject}s that match the specified query
	 * wrapped in an {@link Optional} if the query is successful, or otherwise
	 * {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<List<CurseProject>> searchProjects(CurseSearchQuery query)
			throws CurseException {
		Preconditions.checkNotNull(query, "query should not be null");
		return get(provider -> provider.searchProjects(query));
	}

	/**
	 * Returns a {@link CurseFiles} instance for the specified project ID.
	 *
	 * @param projectID a project ID.
	 * @return a {@link CurseFiles} instance for the specified project ID wrapped in an
	 * {@link Optional} if the project exists, or otherwise {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<CurseFiles<CurseFile>> files(int projectID) throws CurseException {
		CursePreconditions.checkProjectID(projectID, "projectID");
		return get(provider -> provider.files(projectID));
	}

	/**
	 * Returns a {@link CurseFile} instance for the specified project and file ID.
	 *
	 * @param projectID a project ID.
	 * @param fileID a file ID.
	 * @return a {@link CurseFile} instance for the specified project and file ID wrapped in an
	 * {@link Optional} if the file exists, or otherwise {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<CurseFile> file(int projectID, int fileID) throws CurseException {
		CursePreconditions.checkProjectID(projectID, "projectID");
		CursePreconditions.checkFileID(fileID, "fileID");
		return get(provider -> provider.file(projectID, fileID));
	}

	/**
	 * Returns the changelog for the specified project and file ID.
	 *
	 * @param projectID a project ID. This is apparently not necessary, so {@code 0} will suffice.
	 * @param fileID a file ID.
	 * @return an {@link Optional} containing an {@link Element} containing the changelog for the
	 * specified project and file ID or {@link CurseAPI#NO_CHANGELOG_PROVIDED} if none is provided.
	 * If the specified file does not exist, {@link Optional#empty()} is returned.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<Element> fileChangelog(int projectID, int fileID) throws CurseException {
		CursePreconditions.checkProjectID(projectID, "projectID");
		CursePreconditions.checkFileID(fileID, "fileID");
		return get(provider -> provider.fileChangelog(projectID, fileID));
	}

	/**
	 * Returns the download URL for the specified project and file ID.
	 *
	 * @param projectID a project ID.
	 * @param fileID a file ID.
	 * @return the download URL for the specified project and file ID wrapped in an
	 * {@link Optional} if the file exists, or otherwise {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<HttpUrl> fileDownloadURL(int projectID, int fileID)
			throws CurseException {
		CursePreconditions.checkProjectID(projectID, "projectID");
		CursePreconditions.checkFileID(fileID, "fileID");
		return get(provider -> provider.fileDownloadURL(projectID, fileID));
	}

	/**
	 * Downloads the file with the specified project and file ID to the specified {@link Path}.
	 *
	 * @param projectID a project ID.
	 * @param fileID a file ID.
	 * @param path a {@link Path}.
	 * @return {@code true} if the file downloads successfully, or otherwise {@code false}.
	 * @throws CurseException if an error occurs.
	 */
	public static boolean downloadFile(int projectID, int fileID, Path path) throws CurseException {
		CursePreconditions.checkProjectID(projectID, "projectID");
		CursePreconditions.checkFileID(fileID, "fileID");
		Preconditions.checkNotNull(path, "path should not be null");

		final Optional<HttpUrl> optionalURL = fileDownloadURL(projectID, fileID);

		if (!optionalURL.isPresent()) {
			return false;
		}

		OkHttpUtils.download(optionalURL.get(), path);
		return true;
	}

	/**
	 * Downloads the file with the specified project and file ID to the specified directory.
	 *
	 * @param projectID a project ID.
	 * @param fileID a file ID.
	 * @param directory a {@link Path} to a directory.
	 * @return a {@link Path} to the downloaded file wrapped in an {@link Optional} if the
	 * download is successful, or otherwise {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<Path> downloadFileToDirectory(int projectID, int fileID, Path directory)
			throws CurseException {
		CursePreconditions.checkProjectID(projectID, "projectID");
		CursePreconditions.checkFileID(fileID, "fileID");
		Preconditions.checkNotNull(directory, "directory should not be null");

		final Optional<HttpUrl> optionalURL = fileDownloadURL(projectID, fileID);

		if (!optionalURL.isPresent()) {
			return Optional.empty();
		}

		final HttpUrl url = optionalURL.get();
		return Optional.of(OkHttpUtils.downloadToDirectory(
				url, directory, OkHttpUtils.getFileNameFromURLPath(url)
		));
	}

	/**
	 * Returns all games that CurseForge supports.
	 *
	 * @return a mutable {@link Set} containing {@link CurseGame} instances that represent
	 * all games supported by CurseForge wrapped in an {@link Optional} if it can be retrieved,
	 * or otherwise {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<Set<CurseGame>> games() throws CurseException {
		return get(CurseAPIProvider::games);
	}

	/**
	 * Returns a {@link Stream} of all games that CurseForge supports.
	 *
	 * @return a {@link Stream} of all games that CurseForge supports,
	 * or {@link Stream#empty()} if they cannot be retrieved.
	 * @throws CurseException if an error occurs.
	 */
	public static Stream<CurseGame> streamGames() throws CurseException {
		final Optional<Set<CurseGame>> optionalGames = games();
		return optionalGames.map(Set::stream).orElseGet(Stream::empty);
	}

	/**
	 * Returns the CurseForge game with the specified ID.
	 *
	 * @param id a game ID.
	 * @return a {@link CurseGame} instance that represents the CurseForge game with the specified
	 * ID wrapped in an {@link Optional} if it exists, or otherwise {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<CurseGame> game(int id) throws CurseException {
		CursePreconditions.checkGameID(id, "id");
		return get(provider -> provider.game(id));
	}

	/**
	 * Returns all game versions of the game with the specified ID supported by CurseForge.
	 *
	 * @param gameID a game ID.
	 * @param <V> the implementation of {@link CurseGameVersion}.
	 * @return a mutable {@link SortedSet} containing {@link CurseGameVersion} instances that
	 * represent all game versions of the game with the specified ID supported by CurseForge wrapped
	 * in an {@link Optional} if it can be retrieved, or otherwise {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static <V extends CurseGameVersion<?>> Optional<SortedSet<V>> gameVersions(int gameID)
			throws CurseException {
		CursePreconditions.checkGameID(gameID, "gameID");
		return get(provider -> (SortedSet<V>) provider.gameVersions(gameID));
	}

	/**
	 * Returns the game version of the game with the specified ID with the specified version string.
	 *
	 * @param gameID a game ID.
	 * @param versionString a version string. The version string may be empty but should never
	 * be {@code null}.
	 * @param <V> the implementation of {@link CurseGameVersion}.
	 * @return a {@link CurseGameVersion} instance that represents the game version of the game
	 * with the specified ID with the specified version string wrapped in an {@link Optional}
	 * if it exists, or otherwise {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static <V extends CurseGameVersion<?>> Optional<V> gameVersion(
			int gameID, String versionString
	) throws CurseException {
		CursePreconditions.checkGameID(gameID, "gameID");
		Preconditions.checkNotNull(versionString, "versionString should not be null");
		return get(provider -> (V) provider.gameVersion(gameID, versionString));
	}

	/**
	 * Returns all project categories on CurseForge.
	 *
	 * @return a mutable {@link Set} containing {@link CurseCategory} instances that represent
	 * all project categories on CurseForge wrapped in an {@link Optional} if it can be retrieved,
	 * or otherwise {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<Set<CurseCategory>> categories() throws CurseException {
		return get(CurseAPIProvider::categories);
	}

	/**
	 * Returns all categories in a category section.
	 *
	 * @param sectionID a category section ID.
	 * @return a mutable {@link Set} containing {@link CurseCategory} instances that represent
	 * all categories in the category section with the specified ID wrapped in an optional if it
	 * can be retrieved, or otherwise {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<Set<CurseCategory>> categories(int sectionID) throws CurseException {
		CursePreconditions.checkCategorySectionID(sectionID, "sectionID");
		return get(provider -> provider.categories(sectionID));
	}

	/**
	 * Returns a {@link Stream} of all CurseForge categories.
	 *
	 * @return a {@link Stream} of all CurseForge categories, or {@link Stream#empty()} if they
	 * cannot be retrieved.
	 * @throws CurseException if an error occurs.
	 */
	public static Stream<CurseCategory> streamCategories() throws CurseException {
		final Optional<Set<CurseCategory>> optionalCategories = categories();
		return optionalCategories.map(Set::stream).orElseGet(Stream::empty);
	}

	/**
	 * Returns a {@link Stream} of all CurseForge categories in a category section.
	 *
	 * @param sectionID a category section ID.
	 * @return a {@link Stream} of all CurseForge categories in a category section,
	 * or {@link Stream#empty()} if they cannot be retrieved.
	 * @throws CurseException if an error occurs.
	 */
	public static Stream<CurseCategory> streamCategories(int sectionID) throws CurseException {
		final Optional<Set<CurseCategory>> optionalCategories = categories(sectionID);
		return optionalCategories.map(Set::stream).orElseGet(Stream::empty);
	}

	/**
	 * Returns the CurseForge category with the specified ID.
	 *
	 * @param id a category ID.
	 * @return a {@link CurseCategory} instance that represents the CurseForge category with the
	 * specified ID wrapped in an {@link Optional} if it exists, or otherwise
	 * {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public static Optional<CurseCategory> category(int id) throws CurseException {
		CursePreconditions.checkCategoryID(id, "id");
		return get(provider -> provider.category(id));
	}

	/**
	 * Registers a {@link CurseAPIProvider} if has not already been registered.
	 *
	 * @param provider a {@link CurseAPIProvider} instance.
	 * @param firstPriority {@code true} if the {@link CurseAPIProvider} should be put before
	 * all currently registered {@link CurseAPIProvider}s, or otherwise {@code false}.
	 * @return {@code true} if the {@link CurseAPIProvider} was registered,
	 * or otherwise {@code false}.
	 */
	public static boolean addProvider(CurseAPIProvider provider, boolean firstPriority) {
		Preconditions.checkNotNull(provider, "provider should not be null");

		if (providers.contains(provider)) {
			return false;
		}

		if (firstPriority) {
			providers.add(0, provider);
		} else {
			providers.add(provider);
		}

		return true;
	}

	/**
	 * Unregisters a {@link CurseAPIProvider}.
	 *
	 * @param provider a {@link CurseAPIProvider} instance.
	 * @return {@code true} if the {@link CurseAPIProvider} was registered,
	 * or otherwise {@code false}.
	 */
	public static boolean removeProvider(CurseAPIProvider provider) {
		Preconditions.checkNotNull(provider, "provider should not be null");
		return providers.remove(provider);
	}

	/**
	 * Returns an immutable {@link List} of all registered {@link CurseAPIProvider}s.
	 *
	 * @return an immutable {@link List} of all registered {@link CurseAPIProvider}s.
	 */
	public static List<CurseAPIProvider> getProviders() {
		return ImmutableList.copyOf(providers);
	}

	private static <T> Optional<T> get(
			CheckedFunction<CurseAPIProvider, T, CurseException> function
	) throws CurseException {
		if (providers.isEmpty()) {
			logger.warn("No CurseAPIProviders configured");
			return Optional.empty();
		}

		for (CurseAPIProvider provider : providers) {
			final T t = function.apply(provider);

			if (t != null) {
				return Optional.of(t);
			}
		}

		return Optional.empty();
	}
}
