package com.therandomlabs.curseapi.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public final class MiscUtils {
	private MiscUtils() {}

	public static ZonedDateTime parseTime(String time) {
		try {
			return ZonedDateTime.parse(time);
		} catch(DateTimeParseException ex) {
			//Probably an epoch
			final Instant instant =
					Instant.ofEpochSecond(Long.parseLong(time));
			return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
		}
	}
}
