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
