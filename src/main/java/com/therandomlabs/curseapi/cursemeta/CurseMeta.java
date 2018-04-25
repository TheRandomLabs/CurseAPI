package com.therandomlabs.curseapi.cursemeta;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.CurseEventHandling;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.network.NetworkUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public final class CurseMeta {
	public static final String BASE_URL = "https://cursemeta.dries007.net/api/v2/direct/";
	public static final String GET_ADDON = "GetAddOn/";
	public static final String GET_ALL_FILES_FOR_ADDON = "GetAllFilesForAddOn/";
	public static final String GET_ADDON_FILE = "GetAddOnFile/";
	public static final String GET_ADDON_DESCRIPTION = "v2GetAddOnDescription/";
	public static final String GET_CHANGELOG = "v2GetChangeLog/";

	private static final Map<Integer, TRLList<AddOnFile>> cache = new ConcurrentHashMap<>(50);

	private CurseMeta() {}

	public static AddOn getAddOn(int projectID) throws CurseMetaException {
		return get(GET_ADDON + projectID, AddOn.class);
	}

	public static String getAddOnURLString(int projectID) {
		return BASE_URL + GET_ADDON + projectID;
	}

	public static URL getAddOnURL(int projectID) throws CurseException {
		return URLUtils.url(getAddOnURLString(projectID));
	}

	public static TRLList<AddOnFile> getFiles(int projectID) throws CurseMetaException {
		TRLList<AddOnFile> list = cache.get(projectID);

		if(list != null) {
			return list;
		}

		list = new TRLList<>(get(GET_ALL_FILES_FOR_ADDON + projectID, AddOnFile[].class));
		cache.put(projectID, list);
		return list;
	}

	public static String getFilesURLString(int projectID) {
		return BASE_URL + GET_ALL_FILES_FOR_ADDON + projectID;
	}

	public static URL getFilesURL(int projectID) throws CurseException {
		return URLUtils.url(getFilesURLString(projectID));
	}

	public static AddOnFile getFile(int projectID, int fileID) throws CurseMetaException {
		return get(GET_ADDON_FILE + projectID + "/" + fileID, AddOnFile.class);
	}

	public static String getFileURLString(int projectID, int fileID) {
		return BASE_URL + GET_ADDON_FILE + projectID + "/" + fileID;
	}

	public static URL getFileURL(int projectID, int fileID) throws CurseException {
		return URLUtils.url(getFileURLString(projectID, fileID));
	}

	public static Element getDescription(int projectID) throws CurseMetaException {
		return Jsoup.parse(get(GET_ADDON_DESCRIPTION + projectID, String.class));
	}

	public static String getDescriptionURLString(int projectID) {
		return BASE_URL + GET_ADDON_DESCRIPTION + projectID;
	}

	public static URL getDescriptionURL(int projectID) throws CurseException {
		return URLUtils.url(getDescriptionURLString(projectID));
	}

	public static Element getChangelog(int projectID, int fileID) throws CurseMetaException {
		return Jsoup.parse(get(GET_CHANGELOG + projectID + "/" + fileID, String.class));
	}

	public static String getChangelogURLString(int projectID, int fileID) {
		return BASE_URL + GET_CHANGELOG + projectID + "/" + fileID;
	}

	public static URL getChangelogURL(int projectID, int fileID) throws CurseException {
		return URLUtils.url(getChangelogURLString(projectID, fileID));
	}

	public static void clearCache() {
		cache.clear();
	}

	public static void clearCache(int projectID) {
		cache.remove(projectID);
	}

	private static <T> T get(String path, Class<T> clazz) throws CurseMetaException {
		final String url = BASE_URL + path;

		CurseEventHandling.forEach(eventHandler -> eventHandler.preDownloadDocument(url));

		final String json;

		try {
			json = NetworkUtils.read(url);
		} catch(IOException ex) {
			throw new CurseMetaException("An error has occured while reading from: " + url, ex);
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
			throw new CurseMetaException("An error has occurred while parsing data from " +
					"the URL: " + url, ex);
		}
	}
}
