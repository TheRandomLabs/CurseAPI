package com.therandomlabs.curseapi.game;

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
	 * Calling this method is equivalent to calling {@link #id()}.
	 */
	@Override
	public final int hashCode() {
		return id();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method returns {@code true} if and only if the other object is also a
	 * {@link CurseGameVersion} and the value returned by {@link #id()} is the same for both
	 * {@link CurseGameVersion}s.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public final boolean equals(Object object) {
		return this == object ||
				(object instanceof CurseGameVersion && id() == ((CurseGameVersion) object).id());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("gameID", gameID()).
				add("id", id()).
				add("versionString", versionString()).
				toString();
	}

	/**
	 * Returns the ID of the game which this category section belongs in.
	 *
	 * @return the ID of the game which this category section belongs in.
	 */
	public abstract int gameID();

	/**
	 * Returns this category section's ID.
	 *
	 * @return this category section's ID.
	 */
	public abstract int id();

	/**
	 * Returns this game version's version string.
	 *
	 * @return this game version's version string.
	 */
	public abstract String versionString();
}
