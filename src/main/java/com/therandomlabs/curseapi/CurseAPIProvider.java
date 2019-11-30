package com.therandomlabs.curseapi;

public interface CurseAPIProvider {
	CurseProject project(int id) throws CurseException;

	CurseFile file(int projectID, int fileID) throws CurseException;
}
