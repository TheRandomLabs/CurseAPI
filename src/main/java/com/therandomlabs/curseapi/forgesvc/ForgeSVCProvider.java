package com.therandomlabs.curseapi.forgesvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

/**
 * A {@link CurseAPIProvider} that uses the API at {@code https://addons-ecs.forgesvc.net/}
 * used by the Twitch launcher.
 */
public final class ForgeSVCProvider implements CurseAPIProvider {
	/**
	 * The singleton instance of {@link ForgeSVCProvider}.
	 */
	public static final ForgeSVCProvider INSTANCE = new ForgeSVCProvider();

	static final ForgeSVC FORGESVC =
			RetrofitUtils.get("https://addons-ecs.forgesvc.net/").create(ForgeSVC.class);

	private ForgeSVCProvider() {}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseProject project(int id) throws CurseException {
		return RetrofitUtils.execute(FORGESVC.getProject(id));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CurseProject> searchProjects(CurseSearchQuery query) throws CurseException {
		return new ArrayList<>(RetrofitUtils.execute(FORGESVC.searchProjects(
				query.gameID(), query.categorySectionID(), query.categoryID(),
				query.gameVersion(), query.pageIndex(), query.pageSize(),
				query.searchFilter(), query.sortingMethod().id()
		)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseFiles files(int projectID) throws CurseException {
		final Set<ForgeSVCFile> files = RetrofitUtils.execute(FORGESVC.getFiles(projectID));

		for (ForgeSVCFile file : files) {
			file.setProjectID(projectID);
		}

		return new CurseFiles(files);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseFile file(int projectID, int fileID) throws CurseException {
		final ForgeSVCFile file = RetrofitUtils.execute(FORGESVC.getFile(projectID, fileID));
		file.setProjectID(projectID);
		return file;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HttpUrl fileDownloadURL(int projectID, int fileID) throws CurseException {
		return HttpUrl.get(RetrofitUtils.getString(FORGESVC.getFileDownloadURL(projectID, fileID)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<CurseGame> games() throws CurseException {
		return new HashSet<>(RetrofitUtils.execute(FORGESVC.getGames(false)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseGame game(int id) throws CurseException {
		return RetrofitUtils.execute(FORGESVC.getGame(id));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<CurseCategory> categories() throws CurseException {
		return new HashSet<>(RetrofitUtils.execute(FORGESVC.getCategories()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<CurseCategory> categories(int sectionID) throws CurseException {
		return new HashSet<>(RetrofitUtils.execute(FORGESVC.getCategories(sectionID)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseCategory category(int id) throws CurseException {
		return RetrofitUtils.execute(FORGESVC.getCategory(id));
	}
}
