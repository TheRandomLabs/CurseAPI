package com.therandomlabs.curseapi.file;

import com.google.common.base.Preconditions;

/**
 * Represents the release type of a CurseForge file.
 */
public enum CurseReleaseType {
	/**
	 * Release.
	 */
	RELEASE,
	/**
	 * Beta.
	 */
	BETA,
	/**
	 * Alpha.
	 */
	ALPHA;

	private final int id = ordinal() + 1;

	/**
	 * Returns the ID of this release type.
	 *
	 * @return the ID of this release type.
	 */
	public int id() {
		return id;
	}

	/**
	 * Returns whether this release type represents a stability that is equal to or higher than
	 * that of the specified release type.
	 *
	 * @param releaseType another {@link CurseReleaseType}.
	 * @return {@code true} if this release type represents a stability that is equal to or
	 * higher than that of the specified release type, or otherwise {@code false}.
	 */
	public boolean matchesMinimumStability(CurseReleaseType releaseType) {
		return id <= releaseType.id;
	}

	/**
	 * Returns the {@link CurseReleaseType} with the specified ID.
	 *
	 * @param id a release type ID.
	 * @return the {@link CurseReleaseType} with the specified ID.
	 */
	public static CurseReleaseType fromID(int id) {
		Preconditions.checkArgument(id > 0, "id should be positive");
		Preconditions.checkArgument(id <= 3, "id should not be above 3");
		return values()[id - 1];
	}
}
