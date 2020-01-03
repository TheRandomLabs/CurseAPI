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
