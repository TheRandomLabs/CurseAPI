package com.therandomlabs.curseapi.file;

import com.google.common.base.Preconditions;

/**
 * Represents the status of a CurseForge file.
 */
public enum CurseFileStatus {
	/**
	 * Normal.
	 */
	NORMAL(4),
	/**
	 * Rejected.
	 */
	REJECTED(5),
	/**
	 * Deleted.
	 */
	DELETED(7),
	/**
	 * Archived.
	 */
	ARCHIVED(8),
	/**
	 * Unknown.
	 * This value will be removed once all file statuses have been documented.
	 */
	UNKNOWN(-1);

	private final int id;

	CurseFileStatus(int id) {
		this.id = id;
	}

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
	 * Until all file statuses have been documented, {@link #UNKNOWN} will be returned if
	 * an unknown ID is specified.
	 */
	public static CurseFileStatus fromID(int id) {
		Preconditions.checkArgument(id > 0, "id should be above 0");
		Preconditions.checkArgument(id <= 8, "id should not be above 8");

		for (CurseFileStatus status : values()) {
			if (id == status.id) {
				return status;
			}
		}

		return UNKNOWN;
	}
}
