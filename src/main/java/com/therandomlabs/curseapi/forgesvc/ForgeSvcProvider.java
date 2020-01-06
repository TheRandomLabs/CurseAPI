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

package com.therandomlabs.curseapi.forgesvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.therandomlabs.curseapi.CurseAPIProvider;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.CurseSearchQuery;
import com.therandomlabs.curseapi.util.JsoupUtils;
import com.therandomlabs.curseapi.util.RetrofitUtils;
import okhttp3.HttpUrl;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.nodes.Element;

/**
 * A {@link CurseAPIProvider} that uses the API at {@code https://addons-ecs.forgesvc.net/}
 * used by the Twitch launcher.
 * <p>
 * This provider falls back on the methods declared in
 * {@link com.therandomlabs.curseapi.CurseAPI} wherever possible so that default behaviors
 * may be overridden. For example, {@link CurseProject#files()} is implemented by
 * calling {@link com.therandomlabs.curseapi.CurseAPI#files(int)} rather than directly
 * calling {@link #files(int)}.
 * <p>
 * Where possible, this class should not be accessed directly, and the methods declared in
 * {@link com.therandomlabs.curseapi.CurseAPI} should be favored.
 */
public final class ForgeSvcProvider implements CurseAPIProvider {
	/**
	 * The singleton instance of {@link ForgeSvcProvider}.
	 */
	public static final ForgeSvcProvider instance = new ForgeSvcProvider();

	private static final ForgeSvc forgeSVC =
			RetrofitUtils.get("https://addons-ecs.forgesvc.net/").create(ForgeSvc.class);

	private ForgeSvcProvider() {}

	/**
	 * {@inheritDoc}
	 */
	@Nullable
	@Override
	public CurseProject project(int id) throws CurseException {
		return RetrofitUtils.execute(forgeSVC.getProject(id));
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable
	@Override
	public Element projectDescription(int id) throws CurseException {
		final Element element = RetrofitUtils.getElement(forgeSVC.getDescription(id));
		//If the description is empty, we assume that the project does not exist.
		return JsoupUtils.isEmpty(element) ? null : element;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CurseProject> searchProjects(CurseSearchQuery query) throws CurseException {
		final List<ForgeSvcProject> projects = RetrofitUtils.execute(forgeSVC.searchProjects(
				query.gameID(), query.categorySectionID(), query.categoryID(),
				query.gameVersionString(), query.pageIndex(), query.pageSize(),
				query.searchFilter(), query.sortingMethod().id()
		));

		if (projects == null) {
			throw new CurseException("Failed to search projects: " + query);
		}

		return new ArrayList<>(projects);
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable
	@Override
	public CurseFiles<CurseFile> files(int projectID) throws CurseException {
		final Set<ForgeSvcFile> files = RetrofitUtils.execute(forgeSVC.getFiles(projectID));

		if (files == null) {
			return null;
		}

		for (ForgeSvcFile file : files) {
			file.setProjectID(projectID);
		}

		return new CurseFiles<>(files);
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable
	@Override
	public CurseFile file(int projectID, int fileID) throws CurseException {
		final ForgeSvcFile file = RetrofitUtils.execute(forgeSVC.getFile(projectID, fileID));

		if (file == null) {
			return null;
		}

		file.setProjectID(projectID);
		return file;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param projectID a project ID. This is apparently not necessary, so {@code 0} will suffice.
	 */
	@Nullable
	@Override
	public Element fileChangelog(int projectID, int fileID) throws CurseException {
		return RetrofitUtils.getElement(forgeSVC.getChangelog(projectID, fileID));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param projectID a project ID. This is apparently not necessary, so {@code 0} will suffice.
	 */
	@Nullable
	@Override
	public HttpUrl fileDownloadURL(int projectID, int fileID) throws CurseException {
		final String url = RetrofitUtils.getString(forgeSVC.getFileDownloadURL(projectID, fileID));
		return url == null ? null : HttpUrl.get(url);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<CurseGame> games() throws CurseException {
		final Set<ForgeSvcGame> games = RetrofitUtils.execute(forgeSVC.getGames(false));

		if (games == null) {
			throw new CurseException("Failed to retrieve games");
		}

		return new TreeSet<>(games);
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable
	@Override
	public CurseGame game(int id) throws CurseException {
		return RetrofitUtils.execute(forgeSVC.getGame(id));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<CurseCategory> categories() throws CurseException {
		final Set<ForgeSvcCategory> categories = RetrofitUtils.execute(forgeSVC.getCategories());

		if (categories == null) {
			throw new CurseException("Failed to retrieve categories");
		}

		return new TreeSet<>(categories);
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable
	@Override
	public Set<CurseCategory> categories(int sectionID) throws CurseException {
		final Set<ForgeSvcCategory> categories =
				RetrofitUtils.execute(forgeSVC.getCategories(sectionID));
		return categories == null ? null : new TreeSet<>(categories);
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable
	@Override
	public CurseCategory category(int id) throws CurseException {
		return RetrofitUtils.execute(forgeSVC.getCategory(id));
	}
}
