/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2020 TheRandomLabs
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

package com.therandomlabs.curseapi.file;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CursePreconditions;
import com.therandomlabs.curseapi.game.CurseGameVersion;
import com.therandomlabs.curseapi.game.CurseGameVersionGroup;

/**
 * An implementation of {@link Predicate} with several utility methods for {@link CurseFile}s.
 *
 * @see CurseFiles#filter(Predicate)
 */
public class CurseFileFilter implements Cloneable, Predicate<CurseFile> {
	@SuppressWarnings("PMD.LooseCoupling")
	private HashSet<String> gameVersionStrings = new HashSet<>();
	private int newerThan = CurseAPI.MIN_FILE_ID - 1;
	private int olderThan = Integer.MAX_VALUE;
	private CurseReleaseType minimumStability = CurseReleaseType.ALPHA;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseFileFilter clone() {
		try {
			final CurseFileFilter filter = (CurseFileFilter) super.clone();
			filter.gameVersionStrings = new HashSet<>(gameVersionStrings);
			return filter;
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean test(CurseFile file) {
		final Set<String> gameVersionStrings = gameVersionStrings();

		if (!gameVersionStrings.isEmpty() &&
				Collections.disjoint(gameVersionStrings, file.gameVersionStrings())) {
			return false;
		}

		return file.newerThan(newerThan()) && file.olderThan(olderThan()) &&
				file.releaseType().matchesMinimumStability(minimumStability());
	}

	/**
	 * Returns this {@link CurseFileFilter}'s game version strings.
	 *
	 * @return a mutable {@link Set} containing this {@link CurseFileFilter}'s game version strings.
	 */
	@SuppressWarnings("unchecked")
	public Set<String> gameVersionStrings() {
		return (Set<String>) gameVersionStrings.clone();
	}

	/**
	 * Adds the specified game version strings to this {@link CurseFileFilter}.
	 *
	 * @param versions an array of game version strings.
	 * @return this {@link CurseFileFilter}.
	 */
	public CurseFileFilter gameVersionStrings(String... versions) {
		Preconditions.checkNotNull(versions, "versions should not be null");
		return gameVersionStrings(ImmutableSet.copyOf(versions));
	}

	/**
	 * Adds the specified game version strings to this {@link CurseFileFilter}.
	 *
	 * @param versions a collection of game version strings.
	 * @return this {@link CurseFileFilter}.
	 */
	public CurseFileFilter gameVersionStrings(Collection<String> versions) {
		Preconditions.checkNotNull(versions, "versions should not be null");
		gameVersionStrings.addAll(versions);
		return this;
	}

	/**
	 * Adds the specified game versions to this {@link CurseFileFilter}.
	 * "Array" has been appended to the method name to prevent compile-time ambiguity
	 * with {@link #gameVersions(Collection)}.
	 *
	 * @param versions an array of game versions.
	 * @return this {@link CurseFileFilter}.
	 */
	public CurseFileFilter gameVersionsArray(CurseGameVersion<?>... versions) {
		Preconditions.checkNotNull(versions, "versions should not be null");
		return gameVersions(ImmutableSet.copyOf(versions));
	}

	/**
	 * Adds the specified game versions to this {@link CurseFileFilter}.
	 *
	 * @param versions a collection of game versions.
	 * @return this {@link CurseFileFilter}.
	 */
	public CurseFileFilter gameVersions(Collection<? extends CurseGameVersion<?>> versions) {
		Preconditions.checkNotNull(versions, "versions should not be null");

		for (CurseGameVersion<?> version : versions) {
			gameVersionStrings(version.versionString());
		}

		return this;
	}

	/**
	 * Adds the specified game version groups to this {@link CurseFileFilter}.
	 * "Array" has been appended to the method name to prevent compile-time ambiguity
	 * with {@link #gameVersionGroups(Collection)}.
	 *
	 * @param versionGroups an array of game version groups.
	 * @return this {@link CurseFileFilter}.
	 */
	public CurseFileFilter gameVersionGroupsArray(CurseGameVersionGroup<?>... versionGroups) {
		Preconditions.checkNotNull(versionGroups, "versionGroups should not be null");
		return gameVersionGroups(ImmutableSet.copyOf(versionGroups));
	}

	/**
	 * Adds the specified game version groups to this {@link CurseFileFilter}.
	 *
	 * @param versionGroups a collection of game version groups.
	 * @return this {@link CurseFileFilter}.
	 */
	public CurseFileFilter gameVersionGroups(
			Collection<? extends CurseGameVersionGroup<?>> versionGroups
	) {
		Preconditions.checkNotNull(versionGroups, "versionGroups should not be null");

		for (CurseGameVersionGroup<?> versionGroup : versionGroups) {
			gameVersions(versionGroup.versions());
		}

		return this;
	}

	/**
	 * Clears this {@link CurseFileFilter}'s game versions.
	 *
	 * @return this {@link CurseFileFilter}.
	 */
	public CurseFileFilter clearGameVersions() {
		gameVersionStrings.clear();
		return this;
	}

	/**
	 * Returns this {@link CurseFileFilter}'s "newer than" file ID.
	 *
	 * @return this {@link CurseFileFilter}'s "newer than" file ID.
	 * @see #newerThan(int)
	 */
	public int newerThan() {
		return newerThan;
	}

	/**
	 * Sets this {@link CurseFileFilter}'s "newer than" file ID to the ID of the specified
	 * {@link BasicCurseFile}.
	 *
	 * @param file a {@link BasicCurseFile}.
	 * @return this {@link CurseFileFilter}.
	 * @see #newerThan(int)
	 */
	public CurseFileFilter newerThan(BasicCurseFile file) {
		Preconditions.checkNotNull(file, "file should not be null");
		return newerThan(file.id());
	}

	/**
	 * Sets this {@link CurseFileFilter}'s "newer than" file ID.
	 * <p>
	 * If a {@link CurseFile} that is not newer than this {@link CurseFileFilter}'s
	 * "newer than" file ID is passed to {@link #test(CurseFile)}, {@code false} is returned.
	 *
	 * @param fileID a file ID.
	 * @return this {@link CurseFileFilter}.
	 */
	public CurseFileFilter newerThan(int fileID) {
		CursePreconditions.checkFileID(fileID, "fileID");
		Preconditions.checkArgument(
				fileID < olderThan, "fileID should be older than the \"older than\" file"
		);
		newerThan = fileID;
		return this;
	}

	/**
	 * Clears this {@link CurseFileFilter}'s "newer than" file ID.
	 *
	 * @return this {@link CurseFileFilter}.
	 * @see #newerThan(int)
	 */
	public CurseFileFilter clearNewerThan() {
		newerThan = CurseAPI.MIN_FILE_ID - 1;
		return this;
	}

	/**
	 * Returns this {@link CurseFileFilter}'s "older than" file ID.
	 *
	 * @return this {@link CurseFileFilter}'s "older than" file ID.
	 * @see #olderThan(int)
	 */
	public int olderThan() {
		return olderThan;
	}

	/**
	 * Sets this {@link CurseFileFilter}'s "older than" file ID to the ID of the specified
	 * {@link BasicCurseFile}.
	 *
	 * @param file a {@link BasicCurseFile}.
	 * @return this {@link CurseFileFilter}.
	 * @see #olderThan(int)
	 */
	public CurseFileFilter olderThan(BasicCurseFile file) {
		Preconditions.checkNotNull(file, "file should not be null");
		return olderThan(file.id());
	}

	/**
	 * Sets this {@link CurseFileFilter}'s "older than" file ID.
	 * <p>
	 * If a {@link CurseFile} that is not older than this {@link CurseFileFilter}'s
	 * "older than" file ID is passed to {@link #test(CurseFile)}, {@code false} is returned.
	 *
	 * @param fileID a file ID.
	 * @return this {@link CurseFileFilter}.
	 */
	public CurseFileFilter olderThan(int fileID) {
		CursePreconditions.checkFileID(fileID, "fileID");
		Preconditions.checkArgument(
				fileID > newerThan, "fileID should be newer than the \"newer than\" file"
		);
		olderThan = fileID;
		return this;
	}

	/**
	 * Clears this {@link CurseFileFilter}'s "older than" file ID.
	 *
	 * @return this {@link CurseFileFilter}.
	 * @see #olderThan(int)
	 */
	public CurseFileFilter clearOlderThan() {
		olderThan = Integer.MAX_VALUE;
		return this;
	}

	/**
	 * Sets this {@link CurseFileFilter}'s "older than" file ID to the ID of the newer
	 * {@link BasicCurseFile} and its "newer than" file ID to the ID of the older
	 * {@link BasicCurseFile}.
	 *
	 * @param olderFile an older {@link BasicCurseFile}.
	 * @param newerFile a newer {@link BasicCurseFile}.
	 * @return this {@link CurseFileFilter}.
	 * @see #newerThan(int)
	 * @see #olderThan(int)
	 */
	public CurseFileFilter between(BasicCurseFile olderFile, BasicCurseFile newerFile) {
		Preconditions.checkNotNull(olderFile, "olderFile should not be null");
		Preconditions.checkNotNull(newerFile, "newerFile should not be null");
		return between(olderFile.id(), newerFile.id());
	}

	/**
	 * Sets this {@link CurseFileFilter}'s "older than" file ID to the newer file ID
	 * and its "newer than" file ID to the older file ID.
	 *
	 * @param olderFileID an older file ID.
	 * @param newerFileID a newer file ID.
	 * @return this {@link CurseFileFilter}.
	 * @see #newerThan(int)
	 * @see #olderThan(int)
	 */
	public CurseFileFilter between(int olderFileID, int newerFileID) {
		CursePreconditions.checkFileID(olderFileID, "olderFileID");
		CursePreconditions.checkFileID(newerFileID, "newerFileID");
		Preconditions.checkArgument(
				newerFileID > olderFileID, "newerFileID should be newer than olderFileID"
		);
		newerThan(olderFileID);
		olderThan(newerFileID);
		return this;
	}

	/**
	 * Returns this {@link CurseFileFilter}'s minimum stability.
	 *
	 * @return this {@link CurseFileFilter}'s minimum stability.
	 * @see CurseReleaseType#matchesMinimumStability(CurseReleaseType)
	 */
	public CurseReleaseType minimumStability() {
		return minimumStability;
	}

	/**
	 * Sets this {@link CurseFileFilter}'s minimum stability.
	 *
	 * @param releaseType a minimum stability.
	 * @return this {@link CurseFileFilter}.
	 * @see CurseReleaseType#matchesMinimumStability(CurseReleaseType)
	 */
	public CurseFileFilter minimumStability(CurseReleaseType releaseType) {
		Preconditions.checkNotNull(releaseType, "releaseType should not be null");
		minimumStability = releaseType;
		return this;
	}

	/**
	 * Clears this {@link CurseFileFilter}'s minimum stability.
	 *
	 * @return this {@link CurseFileFilter}.
	 * @see CurseReleaseType#matchesMinimumStability(CurseReleaseType)
	 */
	public CurseFileFilter clearMinimumStability() {
		minimumStability = CurseReleaseType.ALPHA;
		return this;
	}

	/**
	 * Applies this {@link CurseFileFilter} on the specified {@link Collection} of
	 * {@link CurseFile}s. This is done by calling {@link Collection#removeIf(Predicate)}
	 * with the {@link Predicate} returned by {@link #negate()}.
	 *
	 * @param files a {@link Collection} of {@link CurseFile}s.
	 * @return {@code true} if any elements were removed, or otherwise {@code false}.
	 */
	public boolean apply(Collection<? extends CurseFile> files) {
		return files.removeIf(negate());
	}
}
