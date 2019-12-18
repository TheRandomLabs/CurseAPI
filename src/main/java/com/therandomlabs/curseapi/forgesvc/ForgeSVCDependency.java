package com.therandomlabs.curseapi.forgesvc;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseDependency;
import com.therandomlabs.curseapi.file.CurseDependencyType;
import com.therandomlabs.curseapi.project.CurseProject;

final class ForgeSVCDependency extends CurseDependency {
	private int addonId;
	private int type;

	//Cache.
	private transient CurseProject project;

	@Override
	public int projectID() {
		return addonId;
	}

	@Override
	public CurseProject project() throws CurseException {
		if (project == null) {
			project = CurseAPI.project(addonId).orElse(null);
		}

		return project;
	}

	@Override
	public void clearProjectCache() {
		project = null;
	}

	@Override
	public CurseDependencyType type() {
		return CurseDependencyType.fromID(type);
	}

	@Override
	public CurseProject toCurseProject() throws CurseException {
		return ForgeSVCProvider.INSTANCE.project(addonId);
	}
}
