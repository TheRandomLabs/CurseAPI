package com.therandomlabs.curseapi.game;

import java.util.Locale;

public interface GameVersion<V extends GameVersion, G extends GameVersionGroup>
		extends Comparable<V> {
	default String id() {
		return toString().toLowerCase(Locale.ENGLISH);
	}

	default G getGroup() {
		return null;
	}

	default boolean newerThan(V version) {
		return compareTo(version) > 0;
	}

	default boolean newerThanOrEqualTo(V version) {
		return compareTo(version) >= 0;
	}

	default boolean olderThan(V version) {
		return compareTo(version) < 0;
	}

	default boolean olderThanOrEqualTo(V version) {
		return compareTo(version) <= 0;
	}
}
