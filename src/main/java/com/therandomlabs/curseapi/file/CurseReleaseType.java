package com.therandomlabs.curseapi.file;

import com.google.common.base.Preconditions;

public enum CurseReleaseType {
	RELEASE,
	BETA,
	ALPHA;

	private static final int MAX_ID = values().length;

	private final int id = ordinal() + 1;

	public int id() {
		return id;
	}

	public boolean matchesMinimumStability(CurseReleaseType releaseType) {
		return id <= releaseType.id;
	}

	public static CurseReleaseType fromID(int id) {
		Preconditions.checkArgument(id > 0, "id should be above 0");
		Preconditions.checkArgument(id <= MAX_ID, "id should not be above %s", MAX_ID);
		return values()[id - 1];
	}
}
