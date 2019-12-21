package com.therandomlabs.curseapi.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Represents a group of game versions.
 * <p>
 * Implementations of this class should be effectively immutable.
 *
 * @param <V> the type of {@link CurseGameVersion}.
 */
public abstract class CurseGameVersionGroup<V extends CurseGameVersion<?>> {
	private static class None<V extends CurseGameVersion<?>> extends CurseGameVersionGroup<V> {
		private final int gameID;

		None(int gameID) {
			this.gameID = gameID;
		}

		@Override
		public int gameID() {
			return gameID;
		}

		@Override
		public String versionString() {
			return "*";
		}

		@Override
		public Set<V> versions() {
			return new HashSet<>();
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The value returned by this method is derived from the values returned by
	 * {@link #gameID()}, {@link #versionString()} and {@link #versions()}.
	 */
	@Override
	public final int hashCode() {
		return Objects.hash(gameID(), versionString(), versions());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method returns {@code true} if and only if the other object is also a
	 * {@link CurseGameVersionGroup} and the values returned by {@link #gameID()},
	 * {@link #versionString()} and {@link #versions()} are the same for both
	 * {@link CurseGameVersionGroup}s.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public final boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CurseGameVersionGroup)) {
			return false;
		}

		final CurseGameVersionGroup group = (CurseGameVersionGroup) object;
		return gameID() == group.gameID() && versionString().equals(group.versionString()) &&
				versions().equals(group.versions());
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
	 * Returns the ID of this game version group's game.
	 *
	 * @return the ID of this game version group's game.
	 */
	public abstract int gameID();

	/**
	 * Returns a version string that represents this game version version group.
	 *
	 * @return a version string that represents this game version group.
	 */
	public abstract String versionString();

	/**
	 * Returns all game versions in this game version group.
	 *
	 * @return a mutable {@link Set} containing all game versions in this game version group.
	 */
	public abstract Set<V> versions();

	/**
	 * Returns all game versions in this game version group as version strings.
	 *
	 * @return a mutable {@link Set} containing all game versions in this game version group as
	 * version strings.
	 */
	public Set<String> versionStrings() {
		return versions().stream().
				map(CurseGameVersion::versionString).
				collect(Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Returns whether this game version group contains any of the specified game versions.
	 *
	 * @param versions an array of game versions.
	 * @return {@code true} if this game version group contains any of the specified game versions,
	 * or otherwise {@code false}.
	 */
	@SafeVarargs
	public final boolean containsAny(V... versions) {
		return containsAny(Arrays.asList(versions));
	}

	/**
	 * Returns whether this game version group contains any of the specified game versions.
	 *
	 * @param versions a collection of game versions.
	 * @return {@code true} if this game version group contains any of the specified game versions,
	 * or otherwise {@code false}.
	 */
	public boolean containsAny(Collection<V> versions) {
		return !Collections.disjoint(versions(), versions);
	}

	/**
	 * Returns whether this game version group contains any of the specified game version strings.
	 *
	 * @param versionStrings an array of game version strings.
	 * @return {@code true} if this game version group contains any of the specified game version
	 * strings, or otherwise {@code false}.
	 */
	public boolean containsAnyStrings(String... versionStrings) {
		return containsAnyStrings(Arrays.asList(versionStrings));
	}

	/**
	 * Returns whether this game version group contains any of the specified game version strings.
	 *
	 * @param versionStrings a {@link Collection} of game version strings.
	 * @return {@code true} if this game version group contains any of the specified game version
	 * strings, or otherwise {@code false}.
	 */
	public boolean containsAnyStrings(Collection<String> versionStrings) {
		return !Collections.disjoint(versionStrings(), versionStrings);
	}

	/**
	 * Returns whether this {@link CurseGameVersionGroup} represents no or an unknown game version
	 * group.
	 *
	 * @return {@code true} if this {@link CurseGameVersionGroup} represents no or an unknown
	 * game version group, or otherwise {@code false}.
	 */
	public final boolean isNone() {
		return this instanceof None;
	}

	/**
	 * Returns a {@link CurseGameVersionGroup} that represents no or an unknown game version
	 * group.
	 *
	 * @param gameID a game ID.
	 * @param <V> the type of {@link CurseGameVersion}.
	 * @return a {@link CurseGameVersionGroup} that represents no or an unknown game version group.
	 */
	public static <V extends CurseGameVersion<?>> CurseGameVersionGroup<V> none(int gameID) {
		return new None<>(gameID);
	}

	/**
	 * Returns a {@link Set} of {@link CurseGameVersionGroup}s for the specified
	 * {@link CurseGameVersion}s.
	 *
	 * @param versions a {@link Collection} of {@link CurseGameVersion}s.
	 * @param <V> the type of {@link CurseGameVersion}.
	 * @return a mutable {@link Set} of {@link CurseGameVersionGroup}s for the specified
	 * {@link CurseGameVersion}s.
	 */
	@SuppressWarnings("unchecked")
	public static <V extends CurseGameVersion<?>> Set<CurseGameVersionGroup<V>> of(
			Collection<? extends V> versions
	) {
		return versions.stream().
				map(version -> (CurseGameVersionGroup<V>) version.versionGroup()).
				filter(group -> !group.isNone()).
				collect(Collectors.toCollection(HashSet::new));
	}
}
