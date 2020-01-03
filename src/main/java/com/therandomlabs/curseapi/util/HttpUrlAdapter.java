/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
	public String toJSON(HttpUrl url) {
		return url.toString();
	}

	/**
	 * Converts the specified JSON string to an {@link HttpUrl}.
	 *
	 * @param url a JSON string.
	 * @return an {@link HttpUrl}.
	 */
	@FromJson
	public HttpUrl fromJSON(String url) {
		return HttpUrl.get(url);
	}
}
