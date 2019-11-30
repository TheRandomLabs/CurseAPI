package com.therandomlabs.curseapi.util;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import okhttp3.HttpUrl;

public final class HttpUrlAdapter {
	public static final HttpUrlAdapter INSTANCE = new HttpUrlAdapter();

	private HttpUrlAdapter() {}

	@ToJson
	public String toJson(HttpUrl url) {
		return url.toString();
	}

	@FromJson
	public HttpUrl fromJson(String url) {
		return HttpUrl.get(url);
	}
}
