/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 TheRandomLabs
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
		Preconditions.checkArgument(id > 0, "id should be positive");
		Preconditions.checkArgument(id <= 8, "id should not be above 8");
		return values()[id - 1];
	}
}
