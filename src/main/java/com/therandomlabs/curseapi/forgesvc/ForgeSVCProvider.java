package com.therandomlabs.curseapi.forgesvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseAPIProvider;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.CurseSearchQuery;
import com.therandomlabs.curseapi.util.RetrofitUtils;
import okhttp3.HttpUrl;
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
public final class ForgeSVCProvider implements CurseAPIProvider {
	/**
	 * The singleton instance of {@link ForgeSVCProvider}.
	 */
	public static final ForgeSVCProvider instance = new ForgeSVCProvider();

	private static final ForgeSVC forgeSVC =
			RetrofitUtils.get("https://addons-ecs.forgesvc.net/").create(ForgeSVC.class);

	private ForgeSVCProvider() {}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseProject project(int id) throws CurseException {
		return RetrofitUtils.execute(forgeSVC.getProject(id));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CurseProject> searchProjects(CurseSearchQuery query) throws CurseException {
		return new ArrayList<>(RetrofitUtils.execute(forgeSVC.searchProjects(
				query.gameID(), query.categorySectionID(), query.categoryID(),
				query.gameVersionString(), query.pageIndex(), query.pageSize(),
				query.searchFilter(), query.sortingMethod().id()
		)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseFiles<CurseFile> files(int projectID) throws CurseException {
		final Set<ForgeSVCFile> files = RetrofitUtils.execute(forgeSVC.getFiles(projectID));

		for (ForgeSVCFile file : files) {
			file.setProjectID(projectID);
		}

		return new CurseFiles<>(files);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseFile file(int projectID, int fileID) throws CurseException {
		final ForgeSVCFile file = RetrofitUtils.execute(forgeSVC.getFile(projectID, fileID));
		file.setProjectID(projectID);
		return file;
	}

	/**
	 * {@inheritDoc}
	 * @param projectID a project ID. This is apparently not necessary, so {@code 0} will suffice.
	 */
	@Override
	public HttpUrl fileDownloadURL(int projectID, int fileID) throws CurseException {
		return HttpUrl.get(RetrofitUtils.getString(forgeSVC.getFileDownloadURL(projectID, fileID)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<CurseGame> games() throws CurseException {
		return new HashSet<>(RetrofitUtils.execute(forgeSVC.getGames(false)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseGame game(int id) throws CurseException {
		return RetrofitUtils.execute(forgeSVC.getGame(id));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<CurseCategory> categories() throws CurseException {
		return new HashSet<>(RetrofitUtils.execute(forgeSVC.getCategories()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<CurseCategory> categories(int sectionID) throws CurseException {
		return new HashSet<>(RetrofitUtils.execute(forgeSVC.getCategories(sectionID)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseCategory category(int id) throws CurseException {
		return RetrofitUtils.execute(forgeSVC.getCategory(id));
	}

	/**
	 * Returns the changelog for the specified project and file ID.
	 *
	 * @param projectID a project ID. This is apparently not necessary, so {@code 0} will suffice.
	 * @param fileID a file ID.
	 * @return an {@link Element} containing the changelog for the specified project and file ID.
	 * @throws CurseException if an error occurs.
	 */
	public Element changelog(int projectID, int fileID) throws CurseException {
		final Element element = RetrofitUtils.getElement(forgeSVC.getChangelog(projectID, fileID)).
				children().first();
		return element == null ? CurseAPI.NO_CHANGELOG_PROVIDED : element;
	}

	/**
	 * Returns the description for the project with the specified ID.
	 *
	 * @param projectID a project ID.
	 * @return an {@link Element} containing the description for the project with the specified ID.
	 * @throws CurseException if an error occurs.
	 */
	public Element description(int projectID) throws CurseException {
		return RetrofitUtils.getElement(forgeSVC.getDescription(projectID)).children().first();
	}
}
