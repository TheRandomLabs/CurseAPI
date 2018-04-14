package com.therandomlabs.curseapi.cursemeta;

import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFileList;
import com.therandomlabs.curseapi.util.CurseEventHandling;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.network.NetworkUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public final class CurseMeta {
	public static final String BASE_URL = "https://cursemeta.dries007.net/api/v2/direct/";
	public static final String GET_ALL_FILES_FOR_ADDON = "GetAllFilesForAddOn/";
	public static final String GET_ADDON_FILE = "GetAddOnFile/";
	public static final String GET_CHANGELOG = "v2GetChangelog/";

	private CurseMeta() {}

	public static TRLList<AddOnFile> getFiles(int projectID) throws CurseMetaException {
		return new TRLList<>(get(GET_ALL_FILES_FOR_ADDON + projectID, AddOnFile[].class));
	}

	public static CurseFileList getCurseFiles(int projectID) throws CurseException {
		final TRLList<AddOnFile> files = getFiles(projectID);
		final CurseFileList curseFiles = new CurseFileList(files.size());

		for(AddOnFile file : files) {
			curseFiles.add(new CurseFile(projectID, file));
		}

		curseFiles.sortByNewest();
		return curseFiles;
	}

	public static AddOnFile getFile(int projectID, int fileID) throws CurseMetaException {
		return get(GET_ADDON_FILE + projectID + "/" + fileID, AddOnFile.class);
	}

	public static CurseFile getCurseFile(int projectID, int fileID) throws CurseException {
		return new CurseFile(projectID, getFile(projectID, fileID));
	}

	public static Element getChangelog(int projectID, int fileID) throws CurseMetaException {
		return Jsoup.parse(get(GET_CHANGELOG + projectID + "/" + fileID, String.class));
	}

	private static <T> T get(String path, Class<T> clazz) throws CurseMetaException {
		final String url = BASE_URL + path;

		CurseEventHandling.forEach(eventHandler -> eventHandler.preDownloadDocument(url));

		final String json;

		try {
			json = NetworkUtils.read(url);
		} catch(IOException ex) {
			throw new CurseMetaException(ex);
		}

		if(json == null) {
			CurseMetaException.unavailable();
		}

		CurseEventHandling.forEach(eventHandler -> eventHandler.postDownloadDocument(url));

		try {
			try {
				final CurseMetaError error = new Gson().fromJson(json, CurseMetaError.class);

				if(error == null) {
					throw new CurseMetaException("Invalid CurseMeta URL: " + url);
				}

				if(error.error) {
					throw new CurseMetaException(error.description, error.status, url);
				}
			} catch(JsonSyntaxException ignored) {}

			return new Gson().fromJson(json, clazz);
		} catch(JsonSyntaxException ex) {
			throw new CurseMetaException(ex);
		}
	}
}
