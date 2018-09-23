package com.therandomlabs.curseapi.cursemeta;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseEventHandling;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.URLs;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.io.NetUtils;
import com.therandomlabs.utils.wrapper.Wrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public final class CurseMeta {
	public static final String URL = "https://cursemeta.dries007.net/";
	public static final String API_STATUS = URL + "api";
	public static final String BASE_URL = URL + "api/v2/direct/";

	public static final String GET_ADDON = "GetAddOn/";
	public static final String GET_ALL_FILES_FOR_ADDON = "GetAllFilesForAddOn/";
	public static final String GET_ADDON_FILE = "GetAddOnFile/";
	public static final String GET_ADDON_DESCRIPTION = "v2GetAddOnDescription/";
	public static final String GET_CHANGELOG = "v2GetChangeLog/";

	private static final Map<Integer, TRLList<AddOnFile>> cache = new ConcurrentHashMap<>(50);

	private CurseMeta() {}

	public static boolean isAvailable() {
		try {
			final CurseMetaStatus status = get(API_STATUS, CurseMetaStatus.class, true);

			if(status == null) {
				return false;
			}

			return status.status.equals("OK") && status.apis.length > 1;
		} catch(CurseMetaException ignored) {}

		return false;
	}

	public static AddOn getAddOn(int projectID) throws CurseMetaException {
		return get(GET_ADDON + projectID, AddOn.class, false);
	}

	public static String getAddOnURLString(int projectID) {
		return BASE_URL + GET_ADDON + projectID;
	}

	public static URL getAddOnURL(int projectID) throws CurseException {
		return URLs.of(getAddOnURLString(projectID));
	}

	public static TRLList<AddOnFile> getFiles(int projectID) throws CurseMetaException {
		TRLList<AddOnFile> list = cache.get(projectID);

		if(list != null) {
			return list;
		}

		list = new TRLList<>(get(GET_ALL_FILES_FOR_ADDON + projectID, AddOnFile[].class, false));
		cache.put(projectID, list);
		return list;
	}

	public static String getFilesURLString(int projectID) {
		return BASE_URL + GET_ALL_FILES_FOR_ADDON + projectID;
	}

	public static URL getFilesURL(int projectID) throws CurseException {
		return URLs.of(getFilesURLString(projectID));
	}

	public static AddOnFile getFile(int projectID, int fileID) throws CurseMetaException {
		return get(GET_ADDON_FILE + projectID + "/" + fileID, AddOnFile.class, false);
	}

	public static String getFileURLString(int projectID, int fileID) {
		return BASE_URL + GET_ADDON_FILE + projectID + "/" + fileID;
	}

	public static URL getFileURL(int projectID, int fileID) throws CurseException {
		return URLs.of(getFileURLString(projectID, fileID));
	}

	public static Element getDescription(int projectID) throws CurseMetaException {
		return Jsoup.parse(get(GET_ADDON_DESCRIPTION + projectID, String.class, false));
	}

	public static String getDescriptionURLString(int projectID) {
		return BASE_URL + GET_ADDON_DESCRIPTION + projectID;
	}

	public static URL getDescriptionURL(int projectID) {
		try {
			return URLs.of(getDescriptionURLString(projectID));
		} catch(CurseException ignored) {}

		return null;
	}

	public static Element getChangelog(int projectID, int fileID) throws CurseMetaException {
		final String data = get(GET_CHANGELOG + projectID + "/" + fileID, String.class, true);

		if(data == null) {
			return Jsoup.parse("No changelog provided");
		}

		return Jsoup.parse(data);
	}

	public static String getChangelogURLString(int projectID, int fileID) {
		return BASE_URL + GET_CHANGELOG + projectID + "/" + fileID;
	}

	public static URL getChangelogURL(int projectID, int fileID) {
		try {
			return URLs.of(getChangelogURLString(projectID, fileID));
		} catch(CurseException ignored) {}

		return null;
	}

	public static void clearCache() {
		cache.clear();
	}

	public static void clearCache(int projectID) {
		cache.remove(projectID);
	}

	private static <T> T get(String path, Class<T> clazz, boolean ignoreNull)
			throws CurseMetaException {
		final Wrapper<T> data = new Wrapper<>();

		try {
			CurseAPI.doWithRetries(() -> data.set(getWithoutRetries(path, clazz, ignoreNull)));
		} catch(CurseException ex) {
			throw (CurseMetaException) ex;
		}

		return data.get();
	}

	private static <T> T getWithoutRetries(String path, Class<T> clazz, boolean ignoreNull)
			throws CurseMetaException {
		URL tempURL = null;

		try {
			tempURL = new URL(BASE_URL + path);
		} catch(MalformedURLException ignored) {
			//This will never happen
		}

		final URL url = tempURL;

		CurseEventHandling.forEach(eventHandler -> eventHandler.preDownloadDocument(url));

		final String json;

		try {
			json = NetUtils.read(url);
		} catch(IOException ex) {
			throw new CurseMetaException("An error occurred while reading from: " + url, ex);
		}

		if(json == null) {
			CurseMetaException.unavailable();
		}

		CurseEventHandling.forEach(eventHandler -> eventHandler.postDownloadDocument(url));

		CurseMetaError error = null;

		try {
			error = new Gson().fromJson(json, CurseMetaError.class);
		} catch(JsonSyntaxException ignored) {}

		if(error == null) {
			if(ignoreNull) {
				return null;
			}

			throw new NullCurseMetaException(url);
		}

		if(error.error) {
			throw new CurseMetaException(error.description, error.status, url);
		}

		try {
			return new Gson().fromJson(json, clazz);
		} catch(JsonSyntaxException ex) {
			throw new CurseMetaException("An error occurred while parsing data from: " + url, ex);
		}
	}
}
