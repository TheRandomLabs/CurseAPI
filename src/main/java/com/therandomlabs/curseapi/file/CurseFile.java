package com.therandomlabs.curseapi.file;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.JsoupUtils;
import com.therandomlabs.curseapi.util.OkHttpUtils;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

/**
 * Represents a CurseForge file.
 * <p>
 * Implementations of this class should be effectively immutable.
 */
public abstract class CurseFile extends BasicCurseFile<CurseFile> {
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
	 */
	@Override
	public Optional<CurseFile> toCurseFile() {
		return Optional.of(this);
	}

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
	 * @return this file's status.
	 */
	public abstract CurseFileStatus status();

	/**
	 * Returns this file's download URL.
	 *
	 * @return this file's download URL.
	 */
	public abstract HttpUrl downloadURL();

	/**
	 * Downloads this file to the specified {@link Path}.
	 *
	 * @param path a {@link Path}.
	 * @throws CurseException if an error occurs.
	 */
	public void download(Path path) throws CurseException {
		OkHttpUtils.download(downloadURL(), path);
	}

	/**
	 * Downloads this file to the specified directory.
	 *
	 * @param directory a {@link Path} to a directory.
	 * @return a {@link Path} to the downloaded file.
	 * @throws CurseException if an error occurs.
	 */
	public Path downloadToDirectory(Path directory) throws CurseException {
		return OkHttpUtils.downloadToDirectory(downloadURL(), directory, nameOnDisk());
	}

	/**
	 * Returns this file's dependencies.
	 *
	 * @return a mutable {@link Set} containing this file's dependencies.
	 */
	public abstract Set<CurseDependency> dependencies();

	/**
	 * Returns this file's dependencies of the specified type.
	 *
	 * @param type a {@link CurseDependencyType}.
	 * @return a mutable {@link Set} containing this file's dependencies of the specified type.
	 */
	public Set<CurseDependency> dependencies(CurseDependencyType type) {
		Preconditions.checkNotNull(type, "type should not be null");
		return dependencies().stream().
				filter(dependency -> dependency.type() == type).
				collect(Collectors.toCollection(HashSet::new));
	}

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
}
