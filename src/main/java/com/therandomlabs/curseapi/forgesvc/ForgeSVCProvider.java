package com.therandomlabs.curseapi.forgesvc;

import com.therandomlabs.curseapi.CurseAPIProvider;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.RetrofitUtils;

public final class ForgeSVCProvider implements CurseAPIProvider {
	public static final ForgeSVCProvider INSTANCE = new ForgeSVCProvider();

	static final ForgeSVC forgeSVC = RetrofitUtils.create(ForgeSVC.class);

	private ForgeSVCProvider() {}

	@Override
	public CurseProject project(int id) throws CurseException {
		return RetrofitUtils.execute(forgeSVC.getProject(id));
	}

	@Override
	public CurseFile file(int projectID, int fileID) {
		return null;
	}
}
