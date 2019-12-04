package com.therandomlabs.curseapi.game;

import java.util.Objects;

import com.google.common.base.MoreObjects;

/**
 * Represents a CurseForge game version.
 * <p>
 * Implementations of this class should be effectively immutable.
 *
 * @param <V> the implementation of this class, which is used as the type parameter for
 * {@link Comparable}.
 */
public abstract class CurseGameVersion<V extends CurseGameVersion<V>> implements Comparable<V> {
	/**
	 * {@inheritDoc}
	 * <p>
	 * The value returned by this method is derived from the values returned by
	 * {@link #gameID()} and {@link #versionString()}.
	 */
	@Override
	public final int hashCode() {
		return Objects.hash(gameID(), versionString());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method returns {@code true} if and only if the other object is also a
	 * {@link CurseGameVersion} and the values returned by {@link #gameID()} and
	 * {@link #versionString()} is the same for both {@link CurseGameVersion}s.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public final boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CurseGameVersion)) {
			return false;
		}

		final CurseGameVersion version = (CurseGameVersion) object;
		return gameID() == version.gameID() && versionString().equals(version.versionString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("gameID", gameID()).
				add("versionString", versionString()).
				toString();
	}

	/**
	 * Returns whether this game version is newer than the specified game version.
	 * @param version a {@link CurseGameVersion} of type {@link V}.
	 * @return {@code true} if this game version is newer than the specified game version,
	 * or otherwise {@code false}.
	 */
	public final boolean newerThan(V version) {
		return compareTo(version) > 0;
	}

	/**
	 * Returns whether this game version is older than the specified game version.
	 * @param version a {@link CurseGameVersion} of type {@link V}.
	 * @return {@code true} if this game version is older than the specified game version,
	 * or otherwise {@code false}.
	 */
	public final boolean olderThan(V version) {
		return compareTo(version) < 0;
	}

	/**
	 * Returns the ID of the game which this category section belongs in.
	 *
	 * @return the ID of the game which this category section belongs in.
	 */
	public abstract int gameID();

	/**
	 * Returns this game version's version string.
	 *
	 * @return this game version's version string.
	 */
	public abstract String versionString();
}
