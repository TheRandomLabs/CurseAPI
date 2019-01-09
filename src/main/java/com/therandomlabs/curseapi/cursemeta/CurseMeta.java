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
	public static final String CURSEMETA_URL = "https://staging-cursemeta.dries007.net/";
	public static final URL FEEDS;
	public static final String BASE_URL = CURSEMETA_URL + "api/v3/direct/";

	public static final String ADDON = "addon/%s";
	public static final String DESCRIPTION = ADDON + "/description";
	public static final String FILES = ADDON + "/files";

	public static final String FILE = ADDON + "/file/%s";
	public static final String CHANGELOG = FILE + "/changelog";

	private static final Map<Integer, TRLList<CMFile>> fileCache = new ConcurrentHashMap<>(50);

	static {
		URL feeds = null;

		try {
			feeds = new URL(CURSEMETA_URL + "api/v3/history/feeds");
		} catch(MalformedURLException ignored) {}

		FEEDS = feeds;
	}

	private CurseMeta() {}

	public static boolean isAvailable() {
		try {
			final CMFeeds feeds = get(FEEDS, CMFeeds.class, true);

			if(feeds == null) {
				return false;
			}

			return feeds.timestamp != 0 && feeds.game_ids.length != 0 && !feeds.intervals.isEmpty();
		} catch(CurseMetaException ignored) {}

		return false;
	}

	public static String getAddonURLString(int projectID) {
		return BASE_URL + String.format(ADDON, projectID);
	}

	public static URL getAddonURL(int projectID) {
		try {
			return new URL(getAddonURLString(projectID));
		} catch(MalformedURLException ignored) {}

		return null;
	}

	public static CMAddon getAddon(int projectID) throws CurseMetaException {
		return get(getAddonURL(projectID), CMAddon.class, false);
	}

	public static String getDescriptionURLString(int projectID) {
		return BASE_URL + String.format(DESCRIPTION, projectID);
	}

	public static URL getDescriptionURL(int projectID) {
		try {
			return URLs.of(getDescriptionURLString(projectID));
		} catch(CurseException ignored) {}

		return null;
	}

	public static Element getDescription(int projectID) throws CurseMetaException {
		return Jsoup.parse(get(getDescriptionURL(projectID), String.class, false));
	}

	public static String getFilesURLString(int projectID) {
		return BASE_URL + String.format(FILES, projectID);
	}

	public static URL getFilesURL(int projectID) {
		try {
			return new URL(getFilesURLString(projectID));
		} catch(MalformedURLException ignored) {}

		return null;
	}

	public static TRLList<CMFile> getFiles(int projectID) throws CurseMetaException {
		TRLList<CMFile> list = fileCache.get(projectID);

		if(list != null) {
			return list;
		}

		list = new TRLList<>(get(getFilesURL(projectID), CMFile[].class, false));
		fileCache.put(projectID, list);
		return list;
	}

	public static String getFileURLString(int projectID, int fileID) {
		return BASE_URL + String.format(FILE, projectID, fileID);
	}

	public static URL getFileURL(int projectID, int fileID) {
		try {
			return new URL(getFileURLString(projectID, fileID));
		} catch(MalformedURLException ignored) {}

		return null;
	}

	public static CMFile getFile(int projectID, int fileID) throws CurseMetaException {
		return get(getFileURL(projectID, fileID), CMFile.class, false);
	}

	public static String getChangelogURLString(int projectID, int fileID) {
		return BASE_URL + String.format(CHANGELOG, projectID, fileID);
	}

	public static URL getChangelogURL(int projectID, int fileID) {
		try {
			return new URL(getChangelogURLString(projectID, fileID));
		} catch(MalformedURLException ignored) {}

		return null;
	}

	public static Element getChangelog(int projectID, int fileID) throws CurseMetaException {
		final String data = get(getChangelogURL(projectID, fileID), String.class, true);

		if(data == null) {
			return Jsoup.parse("No changelog provided");
		}

		return Jsoup.parse(data);
	}

	public static void clearFileCache() {
		fileCache.clear();
	}

	public static void clearFileCache(int projectID) {
		fileCache.remove(projectID);
	}

	private static <T> T get(URL url, Class<T> clazz, boolean ignoreNull)
			throws CurseMetaException {
		final Wrapper<T> data = new Wrapper<>();

		try {
			CurseAPI.doWithRetries(() -> data.set(getWithoutRetries(url, clazz, ignoreNull)));
		} catch(CurseException ex) {
			throw (CurseMetaException) ex;
		}

		return data.get();
	}

	private static <T> T getWithoutRetries(URL url, Class<T> clazz, boolean ignoreNull)
			throws CurseMetaException {
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

		CMError error = null;

		try {
			error = new Gson().fromJson(json, CMError.class);
		} catch(JsonSyntaxException ignored) {}

		if(error == null) {
			if(!clazz.isArray()) {
				if(ignoreNull) {
					return null;
				}

				throw new NullCurseMetaException(url);
			}
		} else if(error.error) {
			throw new CurseMetaException(error.description, error.status, url);
		}

		try {
			return new Gson().fromJson(json, clazz);
		} catch(JsonSyntaxException ex) {
			throw new CurseMetaException("An error occurred while parsing data from: " + url, ex);
		}
	}
}
