/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.therandomlabs.curseapi.game;

import java.util.Objects;

/**
 * Represents a CurseForge game version.
 * <p>
 * Implementations of this class should be effectively immutable.
 *
 * @param <V> the type of {@link CurseGameVersion} that can be compared with instances of this
 * implementation. This is used as the type parameter for {@link Comparable}.
 * Generally, this is the implementation class.
 */
public abstract class CurseGameVersion<V extends CurseGameVersion<?>> implements Comparable<V> {
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
	 * {@link #versionString()} are the same for both {@link CurseGameVersion}s.
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
	 * Returns the value returned by {@link #versionString()}.
	 *
	 * @return the value returned by {@link #versionString()}}.
	 */
	@Override
	public String toString() {
		return versionString();
	}

	/**
	 * Returns this game version's version group.
	 *
	 * @return this game version's version group, or {@link CurseGameVersionGroup#none(int)}
	 * if there is none.
	 */
	public CurseGameVersionGroup<V> versionGroup() {
		return CurseGameVersionGroup.none(gameID());
	}

	/**
	 * Returns whether this game version is newer than the specified game version.
	 *
	 * @param version a {@link CurseGameVersion} of type {@link V}.
	 * @return {@code true} if this game version is newer than the specified game version,
	 * or otherwise {@code false}.
	 */
	public final boolean newerThan(V version) {
		return compareTo(version) > 0;
	}

	/**
	 * Returns whether this game version is older than the specified game version.
	 *
	 * @param version a {@link CurseGameVersion} of type {@link V}.
	 * @return {@code true} if this game version is older than the specified game version,
	 * or otherwise {@code false}.
	 */
	public final boolean olderThan(V version) {
		return compareTo(version) < 0;
	}

	/**
	 * Returns the ID of this game version's game.
	 *
	 * @return the ID of this game version's game.
	 */
	public abstract int gameID();

	/**
	 * Returns this game version's version string.
	 *
	 * @return this game version's version string.
	 */
	public abstract String versionString();
}
