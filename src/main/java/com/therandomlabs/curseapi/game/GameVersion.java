package com.therandomlabs.curseapi.game;

import java.util.Locale;

public interface GameVersion<V extends GameVersion> extends Comparable<V> {
	default String id() {
		return toString().toLowerCase(Locale.ENGLISH);
	}

	default GameVersionGroup getGroup() {
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
