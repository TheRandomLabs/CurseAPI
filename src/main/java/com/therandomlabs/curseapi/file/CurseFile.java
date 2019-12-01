package com.therandomlabs.curseapi.file;

import java.time.ZonedDateTime;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.JsoupUtils;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

/**
 * Represents a CurseForge file.
 * <p>
 * Implementations of this interface should be effectively immutable.
 */
public abstract class CurseFile implements Comparable<CurseFile> {
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
	 * This method returns true if and only if the other object is also a {@link CurseFile} and
	 * the value returned by {@link #id()} is the same for both {@link CurseFile}s.
	 */
	@Override
	public final boolean equals(Object object) {
		return this == object ||
				(object instanceof CurseFile && id() == ((CurseFile) object).id());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("projectID", projectID()).
				add("id", id()).
				add("displayName", displayName()).
				add("downloadURL", downloadURL()).
				toString();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Newer {@link CurseFile}s are represented as being greater than older {@link CurseFile}s.
	 */
	@Override
	public final int compareTo(CurseFile file) {
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
	 * Returns this file's display name.
	 *
	 * @return this file's display name.
	 */
	public abstract String displayName();

	/**
	 * Returns this file's name on disk.
	 *
	 * @return this file's name on disk.
	 */
	public abstract String nameOnDisk();

	/**
	 * Returns this file's upload time.
	 *
	 * @return a {@link ZonedDateTime} instance that represents this file's upload time.
	 */
	public abstract ZonedDateTime uploadTime();

	/**
	 * Returns this file's size in bytes.
	 *
	 * @return this file's size in bytes.
	 */
	public abstract long fileSize();

	/**
	 * Returns this file's release type.
	 *
	 * @return this file's release type.
	 */
	public abstract CurseReleaseType releaseType();

	/**
	 * Returns this file's status.
	 *
	 * @return tihs file's status.
	 */
	public abstract CurseFileStatus status();

	/**
	 * Returns this file's download URL.
	 *
	 * @return this file's download URL.
	 */
	public abstract HttpUrl downloadURL();

	//TODO dependencies, modules, fingerprint

	/**
	 * Returns this file's game versions.
	 *
	 * @return a mutable {@link Set} containing this file's game versions.
	 */
	public abstract Set<String> gameVersions();

	/**
	 * Returns this file's changelog as an {@link Element}.
	 *
	 * @return this file's changelog as an {@link Element}.
	 * @throws CurseException if an error occurs.
	 */
	public abstract Element changelog() throws CurseException;

	/**
	 * Returns this file's changelog as plain text.
	 *
	 * @return this file's changelog as plain text as returned by
	 * {@link JsoupUtils#getPlainText(Element, int)}.
	 * @throws CurseException if an error occurs.
	 */
	public String changelogPlainText() throws CurseException {
		return changelogPlainText(Integer.MAX_VALUE);
	}

	/**
	 * Returns this file's changelog as plain text.
	 *
	 * @param maxLineLength the maximum length of a line. This value is used for word wrapping.
	 * @return this file's changelog as plain text as returned by
	 * {@link JsoupUtils#getPlainText(Element, int)}.
	 * @throws CurseException if an error occurs.
	 */
	public String changelogPlainText(int maxLineLength) throws CurseException {
		Preconditions.checkArgument(maxLineLength > 0, "maxLineLength should be greater than 0");
		return JsoupUtils.getPlainText(changelog(), maxLineLength);
	}

	/**
	 * Returns whether this file is older than the specified file.
	 *
	 * @param file another {@link CurseFile}.
	 * @return {@code true} if this file is older than the specified file,
	 * or otherwise {@code false}.
	 */
	public final boolean olderThan(CurseFile file) {
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
		Preconditions.checkArgument(fileID >= 10, "fileID should not be below 10");
		return id() < fileID;
	}

	/**
	 * Returns whether this file is newer than the specified file.
	 *
	 * @param file another {@link CurseFile}.
	 * @return {@code true} if this file is newer than the specified file,
	 * or otherwise {@code false}.
	 */
	public final boolean newerThan(CurseFile file) {
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
		Preconditions.checkArgument(fileID >= 10, "fileID should not be below 10");
		return id() > fileID;
	}
}
