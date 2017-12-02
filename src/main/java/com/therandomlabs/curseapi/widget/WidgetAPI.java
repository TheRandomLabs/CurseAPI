package com.therandomlabs.curseapi.widget;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.CurseEventHandling;
import com.therandomlabs.utils.network.NetworkUtils;

/**
 * Handles interactions with Curse's widget API.
 * @author TheRandomLabs
 */
public final class WidgetAPI {
	/**
	 * The URL to Curse's widget API.
	 */
	public static final String WIDGET_API_URL = "https://api.cfwidget.com/";

	private static final Map<String, ProjectInfo> cache = new HashMap<>(50);

	private WidgetAPI() {}

	public static ProjectInfo get(String path) throws CurseException {
		if(path.startsWith("/")) {
			path = path.substring(1);
		}

		if(cache.containsKey(path)) {
			return cache.get(path).clone();
		}

		try {
			final String jsonURL = WIDGET_API_URL + path;

			CurseEventHandling.forEach(eventHandler -> eventHandler.preDownloadDocument(jsonURL));
			String json = NetworkUtils.read(jsonURL);
			CurseEventHandling.forEach(eventHandler -> eventHandler.postDownloadDocument(jsonURL));

			if(json == null) {
				CurseException.unavailable();
			}

			ProjectInfo info = new Gson().fromJson(json, ProjectInfo.class);

			if(info.error != null) {
				int tries = 0;
				while(info.error != null && info.error.equals("in_queue") &&
						tries++ < CurseAPI.getMaximumRetries()) {
					//This means this JSON isn't in the database. It should be pretty soon though,
					//so we try again.

					CurseEventHandling.forEach(handler -> handler.retryingJSON(5));

					try {
						Thread.sleep(5000L);
					} catch(InterruptedException ex) {
						throw new CurseException(ex);
					}

					json = NetworkUtils.read(jsonURL);
					info = new Gson().fromJson(json, ProjectInfo.class);
				}

				//If there is still an error
				if(info.error != null) {
					throw new CurseException("The error \"" + info.error +
							"\" has occurred while using the widget API. Error message: " +
							info.message);
				}
			}

			info.json = json;
			cache.put(path, info);
			return info.clone();
		} catch(IOException | JsonSyntaxException ex) {
			if(ex instanceof MalformedURLException || ex.getMessage().contains("400 for URL")) {
				throw new CurseException(path + " is not a valid URL path");
			}

			throw new CurseException(ex);
		}
	}

	public static void clearCache() {
		cache.clear();
	}
}
