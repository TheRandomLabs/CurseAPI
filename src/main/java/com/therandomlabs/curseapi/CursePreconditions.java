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
}
