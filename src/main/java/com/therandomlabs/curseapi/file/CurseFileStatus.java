package com.therandomlabs.curseapi.file;

import com.google.common.base.Preconditions;

/**
 * Represents the status of a CurseForge file.
 */
public enum CurseFileStatus {
	/**
	 * Unknown status.
	 */
	STATUS_1,
	/**
	 * Unknown status.
	 */
	STATUS_2,
	/**
	 * Unknown status.
	 */
	STATUS_3,
	/**
	 * Normal.
	 */
	NORMAL,
	/**
	 * Rejected.
	 */
	REJECTED,
	/**
	 * Unknown status.
	 */
	STATUS_6,
	/**
	 * Deleted.
	 */
	DELETED,
	/**
	 * Archived.
	 */
	ARCHIVED;

	private final int id = ordinal() + 1;

	/**
	 * Returns the ID of this file status.
	 *
	 * @return the ID of this file status.
	 */
	public int id() {
		return id;
	}

	/**
	 * Returns the {@link CurseFileStatus} with the specified ID.
	 *
	 * @param id a file status ID.
	 * @return the {@link CurseFileStatus} with the specified ID.
	 */
	public static CurseFileStatus fromID(int id) {
		Preconditions.checkArgument(id > 0, "id should be above 0");
		Preconditions.checkArgument(id <= 8, "id should not be above 8");
		return values()[id - 1];
	}
}
