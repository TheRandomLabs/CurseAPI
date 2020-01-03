package com.therandomlabs.curseapi.file;

import com.google.common.base.Preconditions;

/**
 * Represents a CurseForge file dependency type.
 */
public enum CurseDependencyType {
	/**
	 * Embedded library.
	 */
	EMBEDDED_LIBRARY,
	/**
	 * Optional dependency.
	 */
	OPTIONAL,
	/**
	 * Required dependency.
	 */
	REQUIRED,
	/**
	 * Tool.
	 */
	TOOL,
	/**
	 * Incompatible.
	 */
	INCOMPATIBLE,
	/**
	 * Included.
	 */
	INCLUDE;

	private final int id = ordinal() + 1;

	/**
	 * Returns the ID of this dependency type.
	 *
	 * @return the ID of this dependency type.
	 */
	public int id() {
		return id;
	}

	/**
	 * Returns the {@link CurseDependencyType} with the specified ID.
	 *
	 * @param id a release type ID.
	 * @return the {@link CurseDependencyType} with the specified ID.
	 */
	public static CurseDependencyType fromID(int id) {
		Preconditions.checkArgument(id > 0, "id should be positive");
		Preconditions.checkArgument(id <= 6, "id should not be above 6");
		return values()[id - 1];
	}
}
