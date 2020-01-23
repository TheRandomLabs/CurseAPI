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

import java.io.IOException;
import java.nio.file.Path;

import com.squareup.moshi.Moshi;
import com.therandomlabs.curseapi.CurseException;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Contains utility methods for working with Moshi.
 */
public final class MoshiUtils {
	/**
	 * A {@link Moshi} instance with adapters for {@link org.jsoup.nodes.Element}s,
	 * {@link okhttp3.HttpUrl}s and {@link java.time.ZonedDateTime}s.
	 */
	public static final Moshi moshi = new Moshi.Builder().
			add(ElementAdapter.INSTANCE).
			add(HttpUrlAdapter.INSTANCE).
			add(ZonedDateTimeAdapter.INSTANCE).
			build();

	private MoshiUtils() {}

	/**
	 * Parses the specified JSON string.
	 *
	 * @param json a JSON string.
	 * @param type the {@link Class} of the type.
	 * @param <T> the type.
	 * @return an object with the specified type.
	 * @throws CurseException if an error occurs.
	 */
	public static <T> T fromJSON(String json, Class<T> type) throws CurseException {
		try {
			return moshi.adapter(type).fromJson(json);
		} catch (IOException ex) {
			throw new CurseException("Failed to read JSON: " + json, ex);
		}
	}

	/**
	 * Parses the specified JSON file.
	 *
	 * @param json a {@link Path} to a JSON file.
	 * @param type the {@link Class} of the type.
	 * @param <T> the type.
	 * @return an object with the specified type.
	 * @throws CurseException if an error occurs.
	 */
	public static <T> T fromJSON(Path json, Class<T> type) throws CurseException {
		try (BufferedSource source = Okio.buffer(Okio.source(json))) {
			return fromJSON(source.readUtf8(), type);
		} catch (IOException ex) {
			throw new CurseException("Failed to read JSON: " + json, ex);
		}
	}

	/**
	 * Converts the specified value to a JSON string.
	 *
	 * @param value a value.
	 * @param <T> the type.
	 * @return a JSON string.
	 */
	public static <T> String toJSON(T value) {
		//CurseForge prefers double space indents
		return moshi.<T>adapter(value.getClass()).indent("  ").toJson(value);
	}

	/**
	 * Converts the specified value to a JSON string and writes it to the specified {@link Path}.
	 *
	 * @param value a value.
	 * @param path a {@link Path}.
	 * @param <T> the type.
	 * @throws CurseException if an I/O error occurs.
	 */
	public static <T> void toJSON(T value, Path path) throws CurseException {
		try (BufferedSink sink = Okio.buffer(Okio.sink(path))) {
			sink.writeUtf8(toJSON(value)).writeUtf8("\n");
		} catch (IOException ex) {
			throw new CurseException("Failed to write JSON: " + path, ex);
		}
	}
}
