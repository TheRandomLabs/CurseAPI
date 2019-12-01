package com.therandomlabs.curseapi.file;

import com.google.common.base.Preconditions;

public enum CurseFileStatus {
	NORMAL(4),
	REJECTED(5),
	DELETED(7),
	ARCHIVED(8),
	UNKNOWN(-1);

	private final int id;

	CurseFileStatus(int id) {
		this.id = id;
	}

	public int id() {
		return id;
	}

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
