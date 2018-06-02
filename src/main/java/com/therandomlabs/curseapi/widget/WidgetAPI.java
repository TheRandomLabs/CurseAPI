package com.therandomlabs.curseapi.widget;

import java.io.IOException;
import java.net.MalformedURLException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseUnavailableException;
import com.therandomlabs.curseapi.util.Documents;
import com.therandomlabs.utils.wrapper.Wrapper;

public final class WidgetAPI {
	public static final String WIDGET_API_URL = "https://api.cfwidget.com/";

	private WidgetAPI() {}

	public static ProjectInfo get(String path) throws CurseException {
		if(path.startsWith("/")) {
			path = path.substring(1);
		}

		final String urlPath = path;
		final String jsonURL = WIDGET_API_URL + path;

		final Wrapper<ProjectInfo> info = new Wrapper<>();
		CurseAPI.doWithRetries(() -> info.set(get(urlPath, jsonURL)));

		return info.get().clone();
	}

	private static ProjectInfo get(String path, String jsonURL) throws CurseException {
		try {
			final String json = Documents.read(jsonURL);

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
}
