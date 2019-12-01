package com.therandomlabs.curseapi.file;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class CurseFileFilter implements Predicate<CurseFile> {
	private final Set<String> gameVersions = new HashSet<>();
	private int newerThan = 9;
	private int olderThan = Integer.MAX_VALUE;
	private CurseReleaseType minimumStability = CurseReleaseType.ALPHA;

	@Override
	public boolean test(CurseFile file) {
		if (!gameVersions.isEmpty() && Collections.disjoint(gameVersions, file.gameVersions())) {
			return false;
		}

		return file.id() > newerThan && file.id() < olderThan &&
				file.releaseType().matchesMinimumStability(minimumStability);
	}

	public Set<String> gameVersions() {
		return new HashSet<>(gameVersions);
	}

	public CurseFileFilter gameVersions(String... versions) {
		Preconditions.checkNotNull(versions, "versions should not be null");
		return gameVersions(ImmutableSet.copyOf(versions));
	}


	public CurseFileFilter gameVersions(Collection<String> versions) {
		Preconditions.checkNotNull(versions, "versions should not be null");
		gameVersions.addAll(versions);
		return this;
	}

	public CurseFileFilter clearGameVersions() {
		gameVersions.clear();
		return this;
	}

	public int newerThan() {
		return newerThan;
	}

	public CurseFileFilter newerThan(CurseFile file) {
		Preconditions.checkNotNull(file, "file should not be null");
		return newerThan(file.id());
	}

	public CurseFileFilter newerThan(int fileID) {
		Preconditions.checkArgument(fileID >= 10, "fileID should not be lower than 10");
		Preconditions.checkArgument(
				fileID < olderThan, "fileID should be older than the \"older than\" file"
		);
		newerThan = fileID;
		return this;
	}

	public CurseFileFilter clearNewerThan() {
		newerThan = 9;
		return this;
	}

	public int olderThan() {
		return olderThan;
	}

	public CurseFileFilter olderThan(CurseFile file) {
		Preconditions.checkNotNull(file, "file should not be null");
		return olderThan(file.id());
	}

	public CurseFileFilter olderThan(int fileID) {
		Preconditions.checkArgument(fileID >= 10, "fileID should not be lower than 10");
		Preconditions.checkArgument(
				fileID > newerThan, "fileID should be newer than the \"newer than\" file"
		);
		olderThan = fileID;
		return this;
	}

	public CurseFileFilter clearOlderThan() {
		olderThan = 9;
		return this;
	}

	public CurseFileFilter between(CurseFile olderFile, CurseFile newerFile) {
		Preconditions.checkNotNull(olderFile, "olderFile should not be null");
		Preconditions.checkNotNull(newerFile, "newerFile should not be null");
		return between(olderFile.id(), newerFile.id());
	}

	public CurseFileFilter between(int olderFileID, int newerFileID) {
		Preconditions.checkArgument(olderFileID >= 10, "olderFileID should not be lower than 10");
		Preconditions.checkArgument(newerFileID >= 10, "newerFileID should not be lower than 10");
		Preconditions.checkArgument(
				newerFileID > olderFileID, "newerFileID should be newer than olderFileID"
		);
		newerThan = olderFileID;
		olderThan = newerFileID;
		return this;
	}

	public CurseReleaseType minimumStability() {
		return minimumStability;
	}

	public CurseFileFilter minimumStability(CurseReleaseType releaseType) {
		Preconditions.checkNotNull(releaseType, "releaseType should not be null");
		minimumStability = releaseType;
		return this;
	}

	public CurseFileFilter clearMinimumStability() {
		minimumStability = CurseReleaseType.ALPHA;
		return this;
	}
}
