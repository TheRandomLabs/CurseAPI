/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2020 TheRandomLabs
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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

/**
 * A Moshi adapter for {@link ZonedDateTime}s.
 */
public final class ZonedDateTimeAdapter {
	/**
	 * The singleton instance of {@link ZonedDateTimeAdapter}.
	 */
	public static final ZonedDateTimeAdapter INSTANCE = new ZonedDateTimeAdapter();

	private ZonedDateTimeAdapter() {}

	/**
	 * Converts the specified {@link ZonedDateTime} to a JSON string.
	 *
	 * @param time a {@link ZonedDateTime}.
	 * @return a JSON string representation of the specified {@link ZonedDateTime}.
	 */
	@ToJson
	public String toJSON(ZonedDateTime time) {
		return time.format(DateTimeFormatter.ISO_INSTANT);
	}

	/**
	 * Converts the specified JSON string to a {@link ZonedDateTime}.
	 *
	 * @param time a JSON string.
	 * @return a {@link ZonedDateTime}.
	 */
	@FromJson
	public ZonedDateTime fromJSON(String time) {
		return ZonedDateTime.parse(time);
	}
}
