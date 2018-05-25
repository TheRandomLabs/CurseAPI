package com.therandomlabs.curseapi.widget;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseUnavailableException;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.utils.wrapper.Wrapper;

/**
 * Handles interactions with Curse's widget API.
 *
 * @author TheRandomLabs
 */
public final class WidgetAPI {
	/**
	 * The URL to Curse's widget API.
	 */
	public static final String WIDGET_API_URL = "https://api.cfwidget.com/";

	private static final Map<String, ProjectInfo> cache = new ConcurrentHashMap<>(50);

	private WidgetAPI() {}

	public static ProjectInfo get(String path) throws CurseException {
		if(path.startsWith("/")) {
			path = path.substring(1);
		}

		if(cache.containsKey(path)) {
			return cache.get(path).clone();
		}

		final String urlPath = path;
		final String jsonURL = WIDGET_API_URL + path;
		final Wrapper<ProjectInfo> info = new Wrapper<>();

		CurseAPI.doWithRetries(() -> info.set(doGet(urlPath, jsonURL)));

		cache.put(path, info.get());
		return info.get().clone();
	}

	private static ProjectInfo doGet(String path, String jsonURL) throws CurseException {
		try {
			final String json = DocumentUtils.read(jsonURL);

			if(json == null) {
				throw new CurseUnavailableException();
			}

			final ProjectInfo info = new Gson().fromJson(json, ProjectInfo.class);

			if(info.error != null) {
				throw new CurseException("The error \"" + info.error + "\" has occurred while " +
						"using the widget API. Error message: " + info.message);
			}

			info.json = json;
			return info;
		} catch(IOException | JsonSyntaxException ex) {
			if(ex instanceof MalformedURLException || ex.getMessage().contains("400 for URL")) {
				throw new CurseException("Invalid widget API path: " + path);
			}

			throw CurseException.fromThrowable(ex);
		}
	}

	public static void clearCache() {
		cache.clear();
	}
}
