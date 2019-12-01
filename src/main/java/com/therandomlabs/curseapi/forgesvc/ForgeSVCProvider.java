package com.therandomlabs.curseapi.forgesvc;

import java.util.Set;

import com.therandomlabs.curseapi.CurseAPIProvider;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseFile;
import com.therandomlabs.curseapi.CurseFiles;
import com.therandomlabs.curseapi.CurseProject;
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
}
