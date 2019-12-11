package com.therandomlabs.curseapi.util;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import okhttp3.HttpUrl;

/**
 * A Moshi adapter for {@link HttpUrl}s.
 */
public final class HttpUrlAdapter {
	/**
	 * The singleton instance of {@link HttpUrlAdapter}.
	 */
	public static final HttpUrlAdapter INSTANCE = new HttpUrlAdapter();

	private HttpUrlAdapter() {}

	/**
	 * Converts the specified {@link HttpUrl} to a JSON string.
	 *
	 * @param url an {@link HttpUrl}.
	 * @return a JSON string representation of the specified {@link HttpUrl}.
	 */
	@ToJson
	public String toJson(HttpUrl url) {
		return url.toString();
	}

	/**
	 * Converts the specified JSON string to an {@link HttpUrl}.
	 *
	 * @param url a JSON string.
	 * @return an {@link HttpUrl}.
	 */
	@FromJson
	public HttpUrl fromJson(String url) {
		return HttpUrl.get(url);
	}
}
