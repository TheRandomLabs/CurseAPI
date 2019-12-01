package com.therandomlabs.curseapi.util;

import com.squareup.moshi.Moshi;

public final class MoshiUtils {
	public static final Moshi MOSHI = new Moshi.Builder().
			add(ElementAdapter.INSTANCE).
			add(HttpUrlAdapter.INSTANCE).
			add(ZonedDateTimeAdapter.INSTANCE).
			build();

	private MoshiUtils() {}
}
