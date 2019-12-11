package com.therandomlabs.curseapi.file;

import java.util.Optional;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;

/**
 * A basic representation of a CurseForge file.
 * Implementations of this interface are not necessarily immutable.
 */
public abstract class BasicCurseFile<F extends BasicCurseFile<F>> implements Comparable<F> {
	/**
	 * An immutable implementation of {@link BasicCurseFile} that represents a CurseForge file
	 * that may or may not exist.
	 */
	public static class Immutable extends BasicCurseFile<Immutable> {
		//These field names should be kept the same for Moshi.
		private final int projectID;
		private final int fileID;

		/**
		 * Constructs an immutable {@link BasicCurseFile} with the specified project and file ID.
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
		public int id() {
			return fileID;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Calling this method is equivalent to calling {@link #id()}.
	 */
	@Override
	public final int hashCode() {
		return id();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method returns {@code true} if and only if the other object is also a
	 * {@link BasicCurseFile} and the value returned by {@link #id()} is the same for both
	 * {@link BasicCurseFile}s.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public final boolean equals(Object object) {
		return this == object ||
				(object instanceof BasicCurseFile && id() == ((BasicCurseFile) object).id());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("projectID", projectID()).
				add("id", id()).
				toString();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Newer files are represented as being greater than older files.
	 */
	@Override
	public final int compareTo(F file) {
		return Integer.compare(file.id(), id());
	}

	/**
	 * Returns the ID of this file's project.
	 *
	 * @return the ID of this file's project.
	 */
	public abstract int projectID();

	/**
	 * Returns this file's ID.
	 *
	 * @return this file's ID.
	 */
	public abstract int id();

	/**
	 * Returns this {@link BasicCurseFile} as a {@link CurseFile}.
	 *
	 * @return a {@link CurseFile} that represents the same CurseForge file as this
	 * {@link BasicCurseFile} wrapped in an {@link Optional} if it exists, or otherwise
	 * {@link Optional#empty()}.
	 * @throws CurseException if an error occurs.
	 */
	public Optional<CurseFile> toCurseFile() throws CurseException {
		return CurseAPI.file(projectID(), id());
	}
}
