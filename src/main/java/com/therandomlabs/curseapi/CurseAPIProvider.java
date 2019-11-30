package com.therandomlabs.curseapi;

import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.project.CurseProject;

public interface CurseAPIProvider {
	CurseProject project(int id) throws CurseException;

	CurseFile file(int projectID, int fileID) throws CurseException;
}
