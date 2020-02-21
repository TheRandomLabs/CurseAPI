/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2020 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
import com.therandomlabs.curseapi.util.JsoupUtils;
import okhttp3.HttpUrl;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.nodes.Element;

/**
 * Provides an implementation of all or a subset of CurseAPI.
 * <p>
 * Implementations of this interface may return {@code null} to signify that a fallback
 * {@link CurseAPIProvider} should be used instead or that an object does not exist on CurseForge.
 * As input validation is performed by the methods in {@link CurseAPI}, it does not need to be
 * performed by implementations of this interface.
 *
 * @see com.therandomlabs.curseapi.forgesvc.ForgeSvcProvider
 * @see com.therandomlabs.curseapi.cfwidget.CFWidgetProvider
 */
public interface CurseAPIProvider {
	/**
	 * Returns a {@link CurseProject} instance for the specified project ID.
	 *
	 * @param id a project ID.
	 * @return a {@link CurseProject} instance for the specified project ID.
	 * @throws CurseException if an error occurs.
	 */
	@Nullable
	default CurseProject project(int id) throws CurseException {
		return null;
	}

	/**
	 * Returns a {@link CurseProject} instance for the project with the specified URL path.
	 *
	 * @param path a project URL path.
	 * @return a {@link CurseProject} instance for the project with the specified URL path.
	 * @throws CurseException if an error occurs.
	 */
	@Nullable
	default CurseProject project(String path) throws CurseException {
		return null;
	}

	/**
	 * Returns the description for the project with the specified ID.
	 *
	 * @param id a project ID.
	 * @return an {@link Element} containing the description for the project with the specified ID.
	 * @throws CurseException if an error occurs.
	 */
	@Nullable
	default Element projectDescription(int id) throws CurseException {
		return null;
	}

	/**
	 * Executes a {@link CurseSearchQuery}.
	 *
	 * @param query a {@link CurseSearchQuery}.
	 * @return a mutable {@link List} of {@link CurseProject}s that match the specified query.
	 * @throws CurseException if an error occurs.
	 */
	@Nullable
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
	@Nullable
	default CurseFiles<CurseFile> files(int projectID) throws CurseException {
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
	@Nullable
	default CurseFile file(int projectID, int fileID) throws CurseException {
		return null;
	}

	/**
	 * Returns the changelog for the specified project and file ID.
	 *
	 * @param projectID a project ID.
	 * @param fileID a file ID.
	 * @return an {@link Element} containing the changelog for the specified project and file ID.
	 * If no changelog is provided for the specified file, an empty {@link Element} is returned.
	 * @throws CurseException if an error occurs.
	 * @see JsoupUtils#emptyElement()
	 */
	@Nullable
	default Element fileChangelog(int projectID, int fileID) throws CurseException {
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
	@Nullable
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
	@Nullable
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
	@Nullable
	default CurseGame game(int id) throws CurseException {
		return null;
	}

	/**
	 * Returns all game versions of the game with the specified ID supported by CurseForge.
	 *
	 * @param gameID a game ID.
	 * @return a mutable {@link SortedSet} containing {@link CurseGameVersion} instances that
	 * represent all game versions of the game with the specified ID supported by CurseForge.
	 * @throws CurseException if an error occurs.
	 */
	@Nullable
	default SortedSet<? extends CurseGameVersion<?>> gameVersions(int gameID)
			throws CurseException {
		return null;
	}

	/**
	 * Returns the game version of the game with the specified ID with the specified version string.
	 *
	 * @param gameID a game ID.
	 * @param versionString a version string. The version string may be empty but should never
	 * be {@code null}.
	 * @return a {@link CurseGameVersion} instance that represents the game version of the game
	 * with the specified ID with the specified version string.
	 * @throws CurseException if an error occurs.
	 */
	@Nullable
	default CurseGameVersion<?> gameVersion(int gameID, String versionString)
			throws CurseException {
		return null;
	}

	/**
	 * Returns all project categories on CurseForge.
	 *
	 * @return a mutable {@link Set} containing {@link CurseCategory} instances that represent
	 * all project categories on CurseForge.
	 * @throws CurseException if an error occurs.
	 */
	@Nullable
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
	@Nullable
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
	@Nullable
	default CurseCategory category(int id) throws CurseException {
		return null;
	}
}
