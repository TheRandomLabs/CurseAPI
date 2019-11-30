package com.therandomlabs.curseapi.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

public final class ZonedDateTimeAdapter {
	public static final ZonedDateTimeAdapter INSTANCE = new ZonedDateTimeAdapter();

	private ZonedDateTimeAdapter() {}

	@ToJson
	public String toJson(ZonedDateTime time) {
		return time.format(DateTimeFormatter.ISO_INSTANT);
	}

	@FromJson
	public ZonedDateTime fromJson(String element) {
		return ZonedDateTime.parse(element);
	}
}
