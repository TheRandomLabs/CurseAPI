package com.therandomlabs.curseapi;

import okhttp3.HttpUrl;

public interface CurseAPIProvider {
	CurseProject project(int id) throws CurseException;

	CurseFiles files(int projectID) throws CurseException;

	CurseFile file(int projectID, int fileID) throws CurseException;

	HttpUrl fileDownloadURL(int projectID, int fileID) throws CurseException;
}
