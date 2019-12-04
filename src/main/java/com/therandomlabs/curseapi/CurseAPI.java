package com.therandomlabs.curseapi;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.forgesvc.ForgeSVCProvider;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.CurseSearchQuery;
import com.therandomlabs.curseapi.util.CheckedFunction;
import okhttp3.HttpUrl;
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

	private static final Logger logger = LoggerFactory.getLogger(CurseAPI.class);

	private static final List<CurseAPIProvider> providers =
			Lists.newArrayList(ForgeSVCProvider.INSTANCE);

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
		Preconditions.checkArgument(
				id >= MIN_PROJECT_ID, "id should not be smaller than %s", MIN_PROJECT_ID
		);
		return get(provider -> provider.project(id));
	}

	/**
	 * Executes a {@link CurseSearchQuery}.
	 *
	 * @param query a {@link CurseSearchQuery}.
	 * @return a {@link List} of {@link CurseProject}s that match the specified query wrapped in an
	 * {@link Optional} if the query is successful, or otherwise {@link Optional#empty()}.
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
	public static Optional<CurseFiles> files(int projectID) throws CurseException {
		Preconditions.checkArgument(
				projectID >= MIN_PROJECT_ID, "projectID should not be smaller than %s",
				MIN_PROJECT_ID
		);
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
		Preconditions.checkArgument(
				projectID >= MIN_PROJECT_ID, "projectID should not be smaller than %s",
				MIN_PROJECT_ID
		);
		Preconditions.checkArgument(
				fileID >= MIN_FILE_ID, "fileID should not be smaller than %s", MIN_FILE_ID
		);
		return get(provider -> provider.file(projectID, fileID));
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
		Preconditions.checkArgument(
				projectID >= MIN_PROJECT_ID, "projectID should not be smaller than %s",
				MIN_PROJECT_ID
		);
		Preconditions.checkArgument(
				fileID >= MIN_FILE_ID, "fileID should not be smaller than %s", MIN_FILE_ID
		);
		return get(provider -> provider.fileDownloadURL(projectID, fileID));
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
		Preconditions.checkArgument(
				sectionID >= MIN_CATEGORY_SECTION_ID, "fileID should not be smaller than %s",
				MIN_CATEGORY_SECTION_ID
		);
		return get(provider -> provider.categories(sectionID));
	}

	/**
	 * Registers a {@link CurseAPIProvider}.
	 *
	 * @param provider a {@link CurseAPIProvider} instance.
	 */
	public static void addProvider(CurseAPIProvider provider) {
		Preconditions.checkNotNull(provider, "provider should not be null");
		Preconditions.checkArgument(
				!providers.contains(provider), "provider should not already have been added"
		);
		providers.add(0, provider);
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
