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

package com.therandomlabs.curseapi;

/**
 * Utility methods for ensuring the validity of CurseForge IDs.
 */
public final class CursePreconditions {
	private CursePreconditions() {}

	/**
	 * Ensures that the specified game ID is valid.
	 *
	 * @param id a game ID.
	 * @param name a name to be used in the error message should the check fail.
	 * @throws IllegalArgumentException if the specified game ID is invalid.
	 * @see CurseAPI#MIN_GAME_ID
	 */
	public static void checkGameID(int id, String name) {
		if (id < CurseAPI.MIN_GAME_ID) {
			throw new IllegalArgumentException(String.format(
					"%s should not be smaller than %s", name, CurseAPI.MIN_GAME_ID
			));
		}
	}

	/**
	 * Ensures that the specified category section ID is valid.
	 *
	 * @param id a game ID.
	 * @param name a name to be used in the error message should the check fail.
	 * @throws IllegalArgumentException if the specified category section ID is invalid.
	 * @see CurseAPI#MIN_CATEGORY_SECTION_ID
	 */
	public static void checkCategorySectionID(int id, String name) {
		if (id < CurseAPI.MIN_CATEGORY_SECTION_ID) {
			throw new IllegalArgumentException(String.format(
					"%s should not be smaller than %s", name, CurseAPI.MIN_CATEGORY_SECTION_ID
			));
		}
	}

	/**
	 * Ensures that the specified category ID is valid.
	 *
	 * @param id a game ID.
	 * @param name a name to be used in the error message should the check fail.
	 * @throws IllegalArgumentException if the specified category ID is invalid.
	 * @see CurseAPI#MIN_CATEGORY_ID
	 */
	public static void checkCategoryID(int id, String name) {
		if (id < CurseAPI.MIN_CATEGORY_ID) {
			throw new IllegalArgumentException(String.format(
					"%s should not be smaller than %s", name, CurseAPI.MIN_CATEGORY_ID
			));
		}
	}

	/**
	 * Ensures that the specified project ID is valid.
	 *
	 * @param id a game ID.
	 * @param name a name to be used in the error message should the check fail.
	 * @throws IllegalArgumentException if the specified project ID is invalid.
	 * @see CurseAPI#MIN_PROJECT_ID
	 */
	public static void checkProjectID(int id, String name) {
		if (id < CurseAPI.MIN_PROJECT_ID) {
			throw new IllegalArgumentException(String.format(
					"%s should not be smaller than %s", name, CurseAPI.MIN_PROJECT_ID
			));
		}
	}

	/**
	 * Ensures that the specified file ID is valid.
	 *
	 * @param id a game ID.
	 * @param name a name to be used in the error message should the check fail.
	 * @throws IllegalArgumentException if the specified file ID is invalid.
	 * @see CurseAPI#MIN_FILE_ID
	 */
	public static void checkFileID(int id, String name) {
		if (id < CurseAPI.MIN_FILE_ID) {
			throw new IllegalArgumentException(String.format(
					"%s should not be smaller than %s", name, CurseAPI.MIN_FILE_ID
			));
		}
	}

	/**
	 * Ensures that the specified project attachment ID is valid.
	 *
	 * @param id a game ID.
	 * @param name a name to be used in the error message should the check fail.
	 * @throws IllegalArgumentException if the specified project attachment ID is invalid.
	 * @see CurseAPI#MIN_ATTACHMENT_ID
	 */
	public static void checkAttachmentID(int id, String name) {
		if (id < CurseAPI.MIN_ATTACHMENT_ID) {
			throw new IllegalArgumentException(String.format(
					"%s should not be smaller than %s", name, CurseAPI.MIN_ATTACHMENT_ID
			));
		}
	}
}
