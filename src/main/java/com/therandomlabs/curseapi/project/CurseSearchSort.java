package com.therandomlabs.curseapi.project;

import com.google.common.base.Preconditions;

/**
 * Represents a sorting method for CurseForge project search results.
 */
public enum CurseSearchSort {
	/**
	 * Featured.
	 */
	FEATURED,
	/**
	 * Popularity.
	 */
	POPULARITY,
	/**
	 * Last updated.
	 */
	LAST_UPDATED,
	/**
	 * Name.
	 */
	NAME,
	/**
	 * Author.
	 */
	AUTHOR,
	/**
	 * Total downloads.
	 */
	TOTAL_DOWNLOADS;

	/**
	 * Returns the ID of this sorting method.
	 *
	 * @return the ID of this sorting method.
	 */
	public int id() {
		return ordinal();
	}

	/**
	 * Returns the {@link CurseSearchSort} with the specified ID.
	 *
	 * @param id a sorting method ID.
	 * @return the {@link CurseSearchSort} with the specified ID.
	 */
	public static CurseSearchSort fromID(int id) {
		Preconditions.checkArgument(id >= 0, "id should not be below 0");
		Preconditions.checkArgument(id <= 5, "id should not be above 5");
		return values()[id];
	}
}
