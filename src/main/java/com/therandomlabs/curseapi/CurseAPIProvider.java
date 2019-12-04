package com.therandomlabs.curseapi;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.game.CurseGameVersion;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.CurseSearchQuery;
import okhttp3.HttpUrl;

/**
 * Provides an implementation of all or a subset of CurseAPI.
 * <p>
 * Implementations of this interface may return {@code null} to signify that a fallback
 * {@link CurseAPIProvider} should be used instead.
 */
public interface CurseAPIProvider {
	/**
	 * Returns a {@link CurseProject} instance for the specified project ID.
	 *
	 * @param id a project ID.
	 * @return a {@link CurseProject} instance for the specified project ID.
	 * @throws CurseException if an error occurs.
	 */
	default CurseProject project(int id) throws CurseException {
		return null;
	}

	/**
	 * Executes a {@link CurseSearchQuery}.
	 *
	 * @param query a {@link CurseSearchQuery}.
	 * @return a {@link List} of {@link CurseProject}s that match the specified query.
	 * @throws CurseException if an error occurs.
	 */
	default List<CurseProject> searchProjects(CurseSearchQuery query) throws CurseException {
		return null;
	}

	/**
	 * Returns a {@link CurseFiles} instance for the specified project ID.
	 *
	 * @param projectID a project ID.
	 * @return a {@link CurseFiles} instance for the specified project ID.
	 * @throws CurseException if an error occurs.
	 */
	default CurseFiles files(int projectID) throws CurseException {
		return null;
	}

	/**
	 * Returns a {@link CurseFile} instance for the specified project and file ID.
	 *
	 * @param projectID a project ID.
	 * @param fileID a file ID.
	 * @return a {@link CurseFile} instance for the specified project and file ID.
	 * @throws CurseException if an error occurs.
	 */
	default CurseFile file(int projectID, int fileID) throws CurseException {
		return null;
	}

	/**
	 * Returns the download URL for the specified project and file ID.
	 *
	 * @param projectID a project ID.
	 * @param fileID a file ID.
	 * @return the download URL for the specified project and file ID.
	 * @throws CurseException if an error occurs.
	 */
	default HttpUrl fileDownloadURL(int projectID, int fileID) throws CurseException {
		return null;
	}

	/**
	 * Returns all games that CurseForge supports.
	 *
	 * @return a mutable {@link Set} containing {@link CurseGame} instances that represent
	 * all games supported by CurseForge.
	 * @throws CurseException if an error occurs.
	 */
	default Set<CurseGame> games() throws CurseException {
		return null;
	}

	/**
	 * Returns the CurseForge game with the specified ID.
	 *
	 * @param id a game ID.
	 * @return a {@link CurseGame} instance that represents the CurseForge game with the specified
	 * ID.
	 * @throws CurseException if an error occurs.
	 */
	default CurseGame game(int id) throws CurseException {
		return null;
	}

	/**
	 * Returns all game versions of the game with the specified ID supported by CurseForge.
	 *
	 * @param gameID a game ID.
	 * @return a {@link SortedSet} containing {@link CurseGameVersion} instances that represent all
	 * game versions of the game with the specified ID supported by CurseForge.
	 * @throws CurseException if an error occurs.
	 */
	@SuppressWarnings("rawtypes")
	default SortedSet<? extends CurseGameVersion> gameVersions(int gameID) throws CurseException {
		return null;
	}

	/**
	 * Returns all project categories on CurseForge.
	 *
	 * @return a mutable {@link Set} containing {@link CurseCategory} instances that represent
	 * all project categories on CurseForge.
	 * @throws CurseException if an error occurs.
	 */
	default Set<CurseCategory> categories() throws CurseException {
		return null;
	}

	/**
	 * Returns all categories in a category section.
	 *
	 * @param sectionID a category section ID.
	 * @return a mutable {@link Set} containing {@link CurseCategory} instances that represent
	 * all categories in the category section with the specified ID.
	 * @throws CurseException if an error occurs.
	 */
	default Set<CurseCategory> categories(int sectionID) throws CurseException {
		return null;
	}

	/**
	 * Returns the CurseForge category with the specified ID.
	 *
	 * @param id a game ID.
	 * @return a {@link CurseCategory} instance that represents the CurseForge category with the
	 * specified ID.
	 * @throws CurseException if an error occurs.
	 */
	default CurseCategory category(int id) throws CurseException {
		return null;
	}
}
