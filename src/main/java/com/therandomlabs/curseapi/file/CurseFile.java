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

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.game.CurseGameVersion;
import com.therandomlabs.curseapi.game.CurseGameVersionGroup;
import com.therandomlabs.curseapi.project.CurseProject;
import okhttp3.HttpUrl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.nodes.Element;

/**
 * Represents a CurseForge file.
 * <p>
 * Implementations of this class should be effectively immutable.
 */
public abstract class CurseFile extends BasicCurseFile implements ExistingCurseFile {
	//Cache.
	@Nullable
	private transient NavigableSet<CurseGameVersion<?>> gameVersions;
	@Nullable
	private transient Element changelog;

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
	 * Returns this file's project as a {@link CurseProject}.
	 * If this {@link CurseFile} implementation caches this value,
	 * it may be refreshed by calling {@link #refreshProject()}.
	 *
	 * @return this file's project as a {@link CurseProject}.
	 * @throws CurseException if an error occurs.
	 */
	@NonNull
	@Override
	public abstract CurseProject project() throws CurseException;

	/**
	 * Returns this file's URL. This method uses the {@link CurseProject} value returned by
	 * {@link #project()} to retrieve the URL, so this value may be refreshed by calling
	 * {@link #refreshProject()}.
	 *
	 * @return this file's URL.
	 * @throws CurseException if an error occurs.
	 */
	@NonNull
	@Override
	public HttpUrl url() throws CurseException {
		return project().fileURL(id());
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
	 * Returns this file's download URL. Note that calling {@link #refreshDownloadURL()}
	 * is redundant.
	 *
	 * @return this file's download URL.
	 */
	@Override
	public abstract HttpUrl downloadURL();

	/**
	 * This method is redundant. The point of clearing the cache is so that data can be
	 * refreshed; however, this usually means a {@link CurseException} can be thrown.
	 * Since {@link #downloadURL()} never throws a {@link CurseException}, there is no need for
	 * this method.
	 *
	 * @return the value returned by {@link #downloadURL()}.
	 */
	@Override
	public final HttpUrl refreshDownloadURL() {
		return downloadURL();
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
	 * Returns this file's Maven dependency string.
	 *
	 * @return this file's Maven dependency string, for example,
	 * {@code "randompatches:randompatches:1.12.2:1.20.1.0"}.
	 * @throws CurseException if an error occurs.
	 */
	public String mavenDependency() throws CurseException {
		return project().slug() + ':' + nameOnDisk().replace('-', ':').replaceAll("\\.[^/.]+$", "");
	}

	/**
	 * Returns whether this file has an alternate file.
	 *
	 * @return {@code true} if this file has an alternate file, or otherwise {@code false}.
	 */
	public boolean hasAlternateFile() {
		return alternateFileID() != 0;
	}

	/**
	 * Returns ID of this file's alternate file. For Minecraft modpacks, this refers
	 * to the server pack. {@link CurseFile} instances cannot be retrieved for alternate files.
	 *
	 * @return the ID of this file's alternate file if it exists, or otherwise {@code 0}.
	 */
	public abstract int alternateFileID();

	/**
	 * Returns this file's alternate file as a {@link CurseAlternateFile}.
	 *
	 * @return a {@link CurseAlternateFile} that represents this file's alternate file.
	 */
	@Nullable
	public abstract CurseAlternateFile alternateFile();

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
	 * {@inheritDoc}
	 */
	@Override
	public Element changelog() throws CurseException {
		if (changelog == null) {
			final Optional<Element> optionalChangelog = CurseAPI.fileChangelog(projectID(), id());

			if (!optionalChangelog.isPresent()) {
				throw new CurseException("Failed to retrieve changelog for file: " + this);
			}

			changelog = optionalChangelog.get();
		}

		return changelog;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Element refreshChangelog() throws CurseException {
		changelog = null;
		return changelog();
	}

	/**
	 * Returns this file's game version groups. This value is obtained by calling
	 * {@link CurseAPI#gameVersionGroups(Collection)} on the value returned by
	 * {@link #gameVersions()}, therefore this value may be refreshed by calling
	 * {@link #refreshGameVersions()}.
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
	 * Returns this file's game versions.
	 * If this {@link CurseFile} implementation caches this value,
	 * it may be refreshed by calling {@link #refreshGameVersions()}.
	 *
	 * @param <V> the implementation of {@link CurseGameVersion}.
	 * @return a mutable {@link NavigableSet} of {@link CurseGameVersion} instances that is
	 * equivalent to the result obtained by calling {@link CurseAPI#gameVersion(int, String)} on
	 * the version strings returned by {@link #gameVersionStrings()}.
	 * If there is no registered {@link com.therandomlabs.curseapi.CurseAPIProvider} implementation
	 * that provides {@link CurseGameVersion}s for this file's game, an empty {@link NavigableSet}
	 * is returned.
	 * @throws CurseException if an error occurs.
	 */
	@SuppressWarnings("unchecked")
	public <V extends CurseGameVersion<?>> NavigableSet<V> gameVersions() throws CurseException {
		if (gameVersions == null) {
			final Set<String> versionStrings = gameVersionStrings();
			gameVersions = new TreeSet<>();
			final int gameID = project().gameID();

			for (String versionString : versionStrings) {
				CurseAPI.<V>gameVersion(gameID, versionString).ifPresent(gameVersions::add);
			}
		}

		return (NavigableSet<V>) gameVersions;
	}

	/**
	 * If this {@link CurseFile} implementation caches the value returned by
	 * {@link #gameVersions()}, this method refreshes this value and returns it.
	 *
	 * @param <V> the implementation of {@link CurseGameVersion}.
	 * @return the refreshed value returned by {@link #gameVersions()}.
	 * @throws CurseException if an error occurs.
	 */
	public <V extends CurseGameVersion<?>> NavigableSet<V> refreshGameVersions()
			throws CurseException {
		gameVersions = null;
		return gameVersions();
	}
}
