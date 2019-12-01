package com.therandomlabs.curseapi.util;

import com.squareup.moshi.Moshi;

/**
 * Contains {@link #MOSHI}.
 */
public final class MoshiUtils {
	/**
	 * A {@link Moshi} instance with adapters for {@link org.jsoup.nodes.Element}s,
	 * {@link okhttp3.HttpUrl}s and {@link java.time.ZonedDateTime}s.
	 */
	public static final Moshi MOSHI = new Moshi.Builder().
			add(ElementAdapter.INSTANCE).
			add(HttpUrlAdapter.INSTANCE).
			add(ZonedDateTimeAdapter.INSTANCE).
			build();

	private MoshiUtils() {}
}
