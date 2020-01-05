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
	public boolean hasMinimumStability(CurseReleaseType releaseType) {
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
