package com.therandomlabs.curseapi.forgesvc;

import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseDependency;
import com.therandomlabs.curseapi.file.CurseDependencyType;
import com.therandomlabs.curseapi.project.CurseProject;

final class ForgeSVCDependency extends CurseDependency {
	private int addonId;
	private int type;

	@Override
	public int projectID() {
		return addonId;
	}

	@Override
	public CurseDependencyType type() {
		return CurseDependencyType.fromID(type);
	}

	@Override
	public CurseProject asProject() throws CurseException {
		return ForgeSVCProvider.INSTANCE.project(addonId);
	}
}
