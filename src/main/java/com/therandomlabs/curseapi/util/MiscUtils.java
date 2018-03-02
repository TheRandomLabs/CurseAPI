package com.therandomlabs.curseapi.util;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import com.google.gson.Gson;
import com.therandomlabs.utils.io.NIOUtils;

public final class MiscUtils {
	private MiscUtils() {}

	public static <T> T fromJson(Path path, Class<T> clazz) throws IOException {
		return new Gson().fromJson(NIOUtils.readFile(path), clazz);
	}

	public static ZonedDateTime parseTime(String time) {
		try {
			return ZonedDateTime.parse(time);
		} catch(DateTimeParseException ex) {
			//Probably an epoch
			return parseTime(Long.parseLong(time));
		}
	}

	public static ZonedDateTime parseTime(long epochSeconds) {
		return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC);
	}
}
