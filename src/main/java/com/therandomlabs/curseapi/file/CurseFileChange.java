package com.therandomlabs.curseapi.file;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.project.CurseProject;

/**
 * Represents a change between two CurseForge file versions, an old file and a new file.
 * If a {@link CurseFileChange} represents a downgrade, the new file is older than the old file.
 *
 * @param <F> the type of {@link BasicCurseFile}.
 */
public class CurseFileChange<F extends BasicCurseFile> {
	private final F oldFile;
	private final F newFile;

	/**
	 * Constructs a {@link CurseFileChange} with the specified old file and new file.
	 * If this {@link CurseFileChange} is to represent a downgrade, the new file may be
	 * older than the old file.
	 *
	 * @param oldFile an old file.
	 * @param newFile a new file.
	 */
	public CurseFileChange(F oldFile, F newFile) {
		Preconditions.checkNotNull(oldFile, "oldFile should not be null");
		Preconditions.checkNotNull(newFile, "newFile should not be null");
		Preconditions.checkArgument(
				oldFile.sameProject(newFile),
				"oldFile and newFile should belong to the same project"
		);
		Preconditions.checkArgument(
				!oldFile.equals(newFile), "oldFile and newFile should represent different files"
		);
		this.oldFile = oldFile;
		this.newFile = newFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return Objects.hash(oldFile, newFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CurseFileChange)) {
			return false;
		}

		final CurseFileChange<? extends BasicCurseFile> fileChange =
				(CurseFileChange<? extends BasicCurseFile>) object;
		return oldFile.equals(fileChange.oldFile) && newFile.equals(fileChange.oldFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("oldFile", oldFile).
				add("newFile", newFile).
				toString();
	}

	/**
	 * Returns the project ID of the old and new files.
	 *
	 * @return the project ID of the old and new files.
	 */
	public int projectID() {
		return oldFile.projectID();
	}

	/**
	 * Returns the project of the old and new files as a {@link CurseProject}.
	 * This is equivalent to calling {@link BasicCurseFile#project()} on the value returned
	 * by {@link #oldFile()}; hence, this value may be cached.
	 *
	 * @return the project of the old and new files as a {@link CurseProject}.
	 * @throws CurseException if an error occurs.
	 */
	public CurseProject project() throws CurseException {
		return oldFile.project();
	}

	/**
	 * Returns the old file. This is not necessarily older than the new file.
	 *
	 * @return the old file.
	 */
	public F oldFile() {
		return oldFile;
	}

	/**
	 * Returns the value returned by {@link #oldFile()} as a {@link CurseFile}.
	 * This value is retrieved by calling {@link CurseProject#files()} on the value returned
	 * by {@link #project()} if it is not already an instance of {@link CurseFile},
	 * so this value may be cached.
	 *
	 * @return the old file as a {@link CurseFile}.
	 * @throws CurseException if an error occurs.
	 */
	public CurseFile oldCurseFile() throws CurseException {
		return asCurseFile(oldFile);
	}

	/**
	 * Returns the new file. This is not necessarily newer than the old file.
	 *
	 * @return the new file.
	 */
	public F newFile() {
		return newFile;
	}

	/**
	 * Returns the value returned by {@link #newFile()} as a {@link CurseFile}.
	 * This value is retrieved by calling {@link CurseProject#files()} on the value returned
	 * by {@link #project()} if it is not already an instance of {@link CurseFile},
	 * so this value may be cached.
	 *
	 * @return the new file as a {@link CurseFile}.
	 * @throws CurseException if an error occurs.
	 */
	public CurseFile newCurseFile() throws CurseException {
		return asCurseFile(newFile);
	}

	/**
	 * Returns the older file.
	 * This may refer to the new file if this {@link CurseFileChange} represents a downgrade.
	 *
	 * @return the older file.
	 */
	public F olderFile() {
		return isDowngrade() ? newFile : oldFile;
	}

	/**
	 * Returns the value returned by {@link #olderFile()} as a {@link CurseFile}.
	 * This value is retrieved by calling {@link CurseProject#files()} on the value returned
	 * by {@link #project()} if it is not already an instance of {@link CurseFile},
	 * so this value may be cached.
	 *
	 * @return the older file as a {@link CurseFile}.
	 * @throws CurseException if an error occurs.
	 */
	public CurseFile olderCurseFile() throws CurseException {
		return asCurseFile(olderFile());
	}

	/**
	 * Returns the newer file.
	 * This may refer to the old file if this {@link CurseFileChange} represents a downgrade.
	 *
	 * @return the newer file.
	 */
	public F newerFile() {
		return isDowngrade() ? oldFile : newFile;
	}

	/**
	 * Returns the value returned by {@link #newerFile()} as a {@link CurseFile}.
	 * This value is retrieved by calling {@link CurseProject#files()} on the value returned
	 * by {@link #project()} if it is not already an instance of {@link CurseFile},
	 * so this value may be cached.
	 *
	 * @return the newer file as a {@link CurseFile}.
	 * @throws CurseException if an error occurs.
	 */
	public CurseFile newerCurseFile() throws CurseException {
		return asCurseFile(newerFile());
	}

	/**
	 * Returns whether this {@link CurseFileChange} represents a downgrade.
	 *
	 * @return {@code true} if the file returned by {@link #oldFile()} is newer than
	 * the file returned by {@link #newFile()}, or otherwise {@code false}.
	 */
	public boolean isDowngrade() {
		return oldFile.newerThan(newFile);
	}

	/**
	 * Returns all files that are chronologically between the old file and the new file.
	 * The older file is excluded, and the newer file is included.
	 *
	 * @return a {@link CurseFiles} that contains all files that are chronologically between
	 * the old file and the new file.
	 * @throws CurseException if an error occurs.
	 */
	public CurseFiles<CurseFile> filesBetween() throws CurseException {
		final CurseFiles<CurseFile> files = project().files();
		new CurseFileFilter().between(olderFile().id(), newerFile().id() + 1).apply(files);
		return files;
	}

	private CurseFile asCurseFile(F file) throws CurseException {
		if (file instanceof CurseFile) {
			return (CurseFile) file;
		}

		final CurseFile curseFile = project().files().fileWithID(file.id());

		if (curseFile == null) {
			throw new CurseException("Could not retrieve as CurseFile: " + file);
		}

		return curseFile;
	}
}
