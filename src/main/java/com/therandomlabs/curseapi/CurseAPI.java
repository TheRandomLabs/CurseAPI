package com.therandomlabs.curseapi;

import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.forgesvc.ForgeSVCProvider;
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
		Preconditions.checkArgument(id >= 10, "id should not be smaller than 10");
		return get(provider -> provider.project(id));
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
		Preconditions.checkArgument(projectID >= 10, "projectID should not be smaller than 10");
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
		Preconditions.checkArgument(projectID >= 10, "projectID should not be smaller than 10");
		Preconditions.checkArgument(fileID >= 10, "fileID should not be smaller than 10");
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
		Preconditions.checkArgument(projectID >= 10, "projectID should not be smaller than 10");
		Preconditions.checkArgument(fileID >= 10, "fileID should not be smaller than 10");
		return get(provider -> provider.fileDownloadURL(projectID, fileID));
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
