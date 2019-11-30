package com.therandomlabs.curseapi;

public interface CurseAPIProvider {
	CurseProject project(int id) throws CurseException;

	CurseFiles files(int projectID) throws CurseException;

	CurseFile file(int projectID, int fileID) throws CurseException;
}
