package com.therandomlabs.curseapi.file;

import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseAPI;

/**
 * Represents a CurseForge file but only contains information about its project ID and file ID.
 * Implementations of this interface are not necessarily immutable.
 */
public interface MinimalCurseFile {
	/**
	 * An immutable implementation of {@link MinimalCurseFile} that represents a CurseForge file
	 * that may or may not exist.
	 */
	class Immutable implements MinimalCurseFile {
		private final int projectID;
		private final int fileID;

		/**
		 * Constructs an immutable {@link MinimalCurseFile} with the specified project and file ID.
		 * The existence of the specified file is not verified.
		 *
		 * @param projectID a project ID.
		 * @param fileID a file ID.
		 */
		public Immutable(int projectID, int fileID) {
			Preconditions.checkArgument(
					projectID >= CurseAPI.MIN_PROJECT_ID, "projectID should not be smaller than %s",
					CurseAPI.MIN_PROJECT_ID
			);
			Preconditions.checkArgument(
					fileID >= CurseAPI.MIN_FILE_ID, "fileID should not be smaller than %s",
					CurseAPI.MIN_FILE_ID
			);
			this.projectID = projectID;
			this.fileID = fileID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int projectID() {
			return projectID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int fileID() {
			return fileID;
		}
	}

	/**
	 * Returns this file's project ID.
	 *
	 * @return this file's project ID.
	 */
	int projectID();

	/**
	 * Returns this file's ID.
	 *
	 * @return this file's ID.
	 */
	int fileID();
}
