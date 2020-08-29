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

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CursePreconditions;
import com.therandomlabs.curseapi.project.CurseProject;
import okhttp3.HttpUrl;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A basic representation of a CurseForge file. Files represented by implementations of this class
 * are not required to exist.
 * Implementations of this interface are not necessarily immutable.
 */
public abstract class BasicCurseFile implements Comparable<BasicCurseFile> {
	/**
	 * An immutable implementation of {@link BasicCurseFile} that represents a CurseForge file
	 * that may or may not exist.
	 */
	public static class Immutable extends BasicCurseFile {
		//These field names should be kept the same for Moshi.
		private final int projectID;
		private final int fileID;

		//Cache.
		@Nullable
		private transient CurseProject project;
		private transient boolean projectRetrieved;

		/**
		 * Constructs an {@link Immutable} with the specified project and file ID.
		 * The existence of the specified file is not verified.
		 *
		 * @param projectID a project ID.
		 * @param fileID a file ID.
		 */
		public Immutable(int projectID, int fileID) {
			CursePreconditions.checkProjectID(projectID, "projectID");
			CursePreconditions.checkFileID(fileID, "fileID");
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
		@Nullable
		@Override
		public CurseProject project() throws CurseException {
			if (!projectRetrieved) {
				project = CurseAPI.project(projectID()).orElse(null);
				projectRetrieved = true;
			}

			return project;
		}

		/**
		 * {@inheritDoc}
		 */
		@Nullable
		@Override
		public CurseProject refreshProject() throws CurseException {
			projectRetrieved = false;
			return project();
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
	public final int compareTo(BasicCurseFile file) {
		return Integer.compare(file.id(), id());
	}

	/**
	 * Returns the ID of this file's project.
	 *
	 * @return the ID of this file's project.
	 */
	public abstract int projectID();

	/**
	 * Returns this file's project as a {@link CurseProject}.
	 * If this {@link BasicCurseFile} implementation caches this value,
	 * it may be refreshed by calling {@link #refreshProject()}.
	 *
	 * @return this file's project as a {@link CurseProject} if it exists,
	 * or otherwise {@code null}.
	 * @throws CurseException if an error occurs.
	 */
	@Nullable
	public abstract CurseProject project() throws CurseException;

	/**
	 * If this {@link BasicCurseFile} implementation caches the value returned by
	 * {@link #project()}, this method refreshes this value and returns it.
	 *
	 * @return the refreshed value returned by {@link #project()}.
	 * @throws CurseException if an error occurs.
	 */
	@Nullable
	public abstract CurseProject refreshProject() throws CurseException;

	/**
	 * Returns whether the specified file belongs to the same project as this file.
	 *
	 * @param file a {@link BasicCurseFile}.
	 * @return {@code true} if the value returned by {@link #projectID()} is the same for both
	 * this {@link BasicCurseFile} and the specified {@link BasicCurseFile},
	 * or otherwise {@code false}.
	 */
	public boolean sameProject(BasicCurseFile file) {
		Preconditions.checkNotNull(file, "file should not be null");
		return projectID() == file.projectID();
	}

	/**
	 * Returns this file's ID.
	 *
	 * @return this file's ID.
	 */
	public abstract int id();

	/**
	 * Returns this file's URL. This method uses the {@link CurseProject} value returned by
	 * {@link #project()} to retrieve the URL, so this value may be refreshed by calling
	 * {@link #refreshProject()}.
	 * The existence and availability of this file are not verified.
	 *
	 * @return this file's URL. If this file's project does not exist, {@code null} is returned.
	 * @throws CurseException if an error occurs.
	 */
	@Nullable
	public HttpUrl url() throws CurseException {
		final CurseProject project = project();
		return project == null ? null : project.fileURL(id());
	}

	/**
	 * Returns whether this file is older than the specified file.
	 *
	 * @param file another {@link CurseFile}.
	 * @return {@code true} if this file is older than the specified file,
	 * or otherwise {@code false}.
	 */
	public final boolean olderThan(BasicCurseFile file) {
		Preconditions.checkNotNull(file, "file should not be null");
		return olderThan(file.id());
	}

	/**
	 * Returns whether this file is older than the file with the specified ID.
	 *
	 * @param fileID a file ID.
	 * @return {@code true} if this file is older than the file with the specified ID,
	 * or otherwise {@code false}.
	 */
	public final boolean olderThan(int fileID) {
		Preconditions.checkArgument(
				fileID >= CurseAPI.MIN_FILE_ID - 1,
				"fileID should not be smaller than " + (CurseAPI.MIN_FILE_ID - 1)
		);
		return id() < fileID;
	}

	/**
	 * Returns whether this file is newer than the specified file.
	 *
	 * @param file another {@link CurseFile}.
	 * @return {@code true} if this file is newer than the specified file,
	 * or otherwise {@code false}.
	 */
	public final boolean newerThan(BasicCurseFile file) {
		Preconditions.checkNotNull(file, "file should not be null");
		return newerThan(file.id());
	}

	/**
	 * Returns whether this file is newer than the file with the specified ID.
	 *
	 * @param fileID a file ID.
	 * @return {@code true} if this file is newer than the file with the specified ID,
	 * or otherwise {@code false}.
	 */
	public final boolean newerThan(int fileID) {
		Preconditions.checkArgument(
				fileID >= CurseAPI.MIN_FILE_ID - 1,
				"fileID should not be smaller than " + (CurseAPI.MIN_FILE_ID - 1)
		);
		return id() > fileID;
	}

	/**
	 * Returns this {@link BasicCurseFile} as a {@link CurseFile}.
	 *
	 * @return a {@link CurseFile} that represents the same CurseForge file as this
	 * {@link BasicCurseFile} if it exists, or otherwise {@code null}.
	 * @throws CurseException if an error occurs.
	 */
	@Nullable
	public CurseFile toCurseFile() throws CurseException {
		return CurseAPI.file(projectID(), id()).orElse(null);
	}
}
