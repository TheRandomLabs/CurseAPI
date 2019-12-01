package com.therandomlabs.curseapi.file;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.therandomlabs.curseapi.CurseAPI;

/**
 * An implementation of {@link Predicate} with several utility methods for {@link CurseFile}s.
 *
 * @see CurseFiles#filter(Predicate)
 */
public class CurseFileFilter implements Predicate<CurseFile> {
	private final Set<String> gameVersions = new HashSet<>();
	private int newerThan = 9;
	private int olderThan = Integer.MAX_VALUE;
	private CurseReleaseType minimumStability = CurseReleaseType.ALPHA;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean test(CurseFile file) {
		if (!gameVersions.isEmpty() && Collections.disjoint(gameVersions, file.gameVersions())) {
			return false;
		}

		return file.newerThan(newerThan) && file.olderThan(olderThan) &&
				file.releaseType().matchesMinimumStability(minimumStability);
	}

	/**
	 * Returns this {@link CurseFileFilter}'s game versions.
	 *
	 * @return a mutable {@link Set} containing this {@link CurseFileFilter}'s game versions.
	 */
	public Set<String> gameVersions() {
		return new HashSet<>(gameVersions);
	}

	/**
	 * Sets this {@link CurseFileFilter}'s game versions.
	 *
	 * @param versions an array of game versions.
	 * @return this {@link CurseFileFilter}.
	 */
	public CurseFileFilter gameVersions(String... versions) {
		Preconditions.checkNotNull(versions, "versions should not be null");
		return gameVersions(ImmutableSet.copyOf(versions));
	}

	/**
	 * Sets this {@link CurseFileFilter}'s game versions.
	 *
	 * @param versions a collection of game versions.
	 * @return this {@link CurseFileFilter}.
	 */
	public CurseFileFilter gameVersions(Collection<String> versions) {
		Preconditions.checkNotNull(versions, "versions should not be null");
		gameVersions.addAll(versions);
		return this;
	}

	/**
	 * Clears this {@link CurseFileFilter}'s game versions.
	 *
	 * @return this {@link CurseFileFilter}.
	 */
	public CurseFileFilter clearGameVersions() {
		gameVersions.clear();
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
	 * {@link CurseFile}.
	 *
	 * @param file a {@link CurseFile}.
	 * @return this {@link CurseFileFilter}.
	 * @see #newerThan(int)
	 */
	public CurseFileFilter newerThan(CurseFile file) {
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
		Preconditions.checkArgument(
				fileID >= CurseAPI.MIN_FILE_ID, "fileID should not be lower than %s",
				CurseAPI.MIN_FILE_ID
		);
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
		newerThan = 9;
		return this;
	}

	/**
	 * Sets this {@link CurseFileFilter}'s "older than" file ID to the ID of the specified
	 * {@link CurseFile}.
	 *
	 * @param file a {@link CurseFile}.
	 * @return this {@link CurseFileFilter}.
	 * @see #olderThan(int)
	 */
	public CurseFileFilter olderThan(CurseFile file) {
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
		Preconditions.checkArgument(
				fileID >= CurseAPI.MIN_FILE_ID, "fileID should not be lower than %s",
				CurseAPI.MIN_FILE_ID
		);
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
		olderThan = 9;
		return this;
	}

	/**
	 * Sets this {@link CurseFileFilter}'s "older than" file ID to the ID of the newer
	 * {@link CurseFile} and its "newer than" file ID to the ID of the older {@link CurseFile}.
	 *
	 * @param olderFile an older {@link CurseFile}.
	 * @param newerFile a newer {@link CurseFile}.
	 * @return this {@link CurseFileFilter}.
	 * @see #newerThan(int)
	 * @see #olderThan(int)
	 */
	public CurseFileFilter between(CurseFile olderFile, CurseFile newerFile) {
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
		Preconditions.checkArgument(
				olderFileID >= CurseAPI.MIN_FILE_ID, "olderFileID should not be lower than %s",
				CurseAPI.MIN_FILE_ID
		);
		Preconditions.checkArgument(
				newerFileID >= CurseAPI.MIN_FILE_ID, "newerFileID should not be lower than %s",
				CurseAPI.MIN_FILE_ID
		);
		Preconditions.checkArgument(
				newerFileID > olderFileID, "newerFileID should be newer than olderFileID"
		);
		newerThan = olderFileID;
		olderThan = newerFileID;
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
}
