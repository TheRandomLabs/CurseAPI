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

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.game.CurseGameVersion;
import com.therandomlabs.curseapi.game.CurseGameVersionGroup;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.JsoupUtils;
import com.therandomlabs.curseapi.util.OkHttpUtils;
import okhttp3.HttpUrl;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.nodes.Element;

/**
 * Represents a CurseForge file.
 * <p>
 * Implementations of this class should be effectively immutable.
 */
public abstract class CurseFile extends BasicCurseFile {
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
	public CurseFile toCurseFile() {
		return this;
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
	 * Returns this file's Maven dependency string.
	 *
	 * @return this file's Maven dependency string, for example,
	 * {@code "randompatches:randompatches:1.12.2:1.20.1.0"}.
	 * If this file's project does not exist, {@code null} is returned.
	 * @throws CurseException if an error occurs.
	 */
	@Nullable
	public String mavenDependency() throws CurseException {
		final CurseProject project = project();

		if (project == null) {
			return null;
		}

		return project.slug() + ':' + nameOnDisk().replace('-', ':').replaceAll("\\.[^/.]+$", "");
	}

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
	 * Returns this file's game version strings.
	 *
	 * @return a mutable {@link Set} containing this file's game version strings.
	 */
	public abstract Set<String> gameVersionStrings();

	/**
	 * Returns this file's game versions. This value may be cached.
	 *
	 * @param <V> the type of {@link CurseGameVersion}.
	 * @return a mutable {@link SortedSet} of {@link CurseGameVersion} instances that is equivalent
	 * to the result obtained by calling {@link CurseAPI#gameVersion(int, String)} on the version
	 * strings returned by {@link #gameVersionStrings()}.
	 * If there is no registered {@link com.therandomlabs.curseapi.CurseAPIProvider} implementation
	 * that provides {@link CurseGameVersion}s for this file's game, an empty {@link SortedSet} is
	 * returned.
	 * @throws CurseException if an error occurs.
	 */
	public abstract <V extends CurseGameVersion<?>> SortedSet<V> gameVersions()
			throws CurseException;

	/**
	 * Returns this file's game version groups. This value is obtained by calling
	 * {@link CurseAPI#gameVersionGroups(Collection)} on the value returned by
	 * {@link #gameVersions()}, therefore this value may be cached.
	 *
	 * @param <V> the type of {@link CurseGameVersion}.
	 * @return a mutable {@link Set} containing this file's game version groups as
	 * {@link CurseGameVersionGroup}s.
	 * @throws CurseException if an error occurs.
	 */
	public <V extends CurseGameVersion<?>> Set<CurseGameVersionGroup<V>> gameVersionGroups()
			throws CurseException {
		return CurseAPI.gameVersionGroups(gameVersions());
	}

	/**
	 * If this {@link CurseFile} implementation caches the value returned by
	 * {@link #gameVersions()}, this method clears this cached value.
	 */
	public abstract void clearGameVersionsCache();

	/**
	 * Returns this file's changelog. This value may be cached.
	 *
	 * @return an {@link Element} containing this file's changelog. If a changelog is not provided,
	 * an empty {@link Element} is returned.
	 * @throws CurseException if an error occurs.
	 * @see #clearChangelogCache()
	 * @see JsoupUtils#emptyElement()
	 */
	public abstract Element changelog() throws CurseException;

	/**
	 * Returns this file's changelog as plain text. This value may be cached.
	 *
	 * @return this file's changelog as plain text. If a changelog is not provided, an empty
	 * string is returned.
	 * @throws CurseException if an error occurs.
	 * @see #clearChangelogCache()
	 * @see JsoupUtils#getPlainText(Element, int)
	 */
	public String changelogPlainText() throws CurseException {
		return changelogPlainText(Integer.MAX_VALUE);
	}

	/**
	 * Returns this file's changelog as plain text. This value may be cached.
	 *
	 * @param maxLineLength the maximum length of a line. This value is used for word wrapping.
	 * @return this file's changelog as plain text. If a changelog is not provided, an empty
	 * string is returned.
	 * @throws CurseException if an error occurs.
	 * @see #clearChangelogCache()
	 * @see JsoupUtils#getPlainText(Element, int)
	 */
	public String changelogPlainText(int maxLineLength) throws CurseException {
		Preconditions.checkArgument(maxLineLength > 0, "maxLineLength should be greater than 0");
		return JsoupUtils.getPlainText(changelog(), maxLineLength).trim();
	}

	/**
	 * If this {@link CurseFile} implementation caches the value returned by {@link #changelog()},
	 * this method clears this cached value.
	 */
	public abstract void clearChangelogCache();
}
