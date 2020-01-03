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
