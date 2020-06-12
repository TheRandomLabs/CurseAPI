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
import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.JsoupUtils;
import com.therandomlabs.curseapi.util.OkHttpUtils;
import okhttp3.HttpUrl;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.nodes.Element;

/**
 * Implementations of this interface should represent CurseForge files that are known to exist.
 * Implementors of this class should also contain a non-{@code null} implementation of
 * {@link BasicCurseFile#project()} and {@link BasicCurseFile#url()}.
 */
public interface ExistingCurseFile {
	/**
	 * An immutable representation of a CurseForge file that is known to exist.
	 * This class is most useful for representing alternate files that are known to exist but
	 * of which the main file IDs are not known, or for files which are known to exist in general.
	 */
	class Existing extends BasicCurseFile.Immutable implements ExistingCurseFile {
		//Cache.
		@Nullable
		private transient HttpUrl downloadURL;
		@Nullable
		private transient Element changelog;

		/**
		 * Constructs an {@link Existing} with the specified project and file ID.
		 * While the specified file is not verified to exist, it is assumed to.
		 *
		 * @param projectID a project ID.
		 * @param fileID a file ID.
		 */
		public Existing(int projectID, int fileID) {
			super(projectID, fileID);
		}

		/**
		 * {@inheritDoc}
		 */
		@Nonnull
		@Override
		public CurseProject project() throws CurseException {
			final CurseProject project = super.project();

			if (project == null) {
				throw new CurseException("Project does not exist: " + this);
			}

			return project;
		}

		/**
		 * {@inheritDoc}
		 */
		@Nonnull
		@Override
		public HttpUrl url() throws CurseException {
			return project().fileURL(id());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public HttpUrl downloadURL() throws CurseException {
			if (downloadURL == null) {
				final Optional<HttpUrl> optionalDownloadURL =
						CurseAPI.fileDownloadURL(projectID(), id());

				if (!optionalDownloadURL.isPresent()) {
					throw new CurseException("File does not exist: " + this);
				}

				downloadURL = optionalDownloadURL.get();
			}

			return downloadURL;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public HttpUrl refreshDownloadURL() throws CurseException {
			downloadURL = null;
			return downloadURL();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Element changelog() throws CurseException {
			if (changelog == null) {
				final Optional<Element> optionalChangelog =
						CurseAPI.fileChangelog(projectID(), id());

				if (!optionalChangelog.isPresent()) {
					throw new CurseException("File does not exist: " + this);
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
	}

	/**
	 * Returns this file's download URL.
	 * If this implementation of {@link ExistingCurseFile} caches this value,
	 * it may be refreshed by calling {@link #refreshDownloadURL()}.
	 *
	 * @return this file's download URL.
	 * @throws CurseException if an error occurs.
	 */
	HttpUrl downloadURL() throws CurseException;

	/**
	 * Downloads this file to the specified {@link Path}.
	 *
	 * @param path a {@link Path}.
	 * @throws CurseException if an error occurs.
	 */
	default void download(Path path) throws CurseException {
		OkHttpUtils.download(downloadURL(), path);
	}

	/**
	 * Downloads this file to the specified directory.
	 *
	 * @param directory a {@link Path} to a directory.
	 * @return a {@link Path} to the downloaded file.
	 * @throws CurseException if an error occurs.
	 */
	default Path downloadToDirectory(Path directory) throws CurseException {
		final HttpUrl url = downloadURL();
		return OkHttpUtils.downloadToDirectory(
				url, directory, OkHttpUtils.getFileNameFromURLPath(url)
		);
	}

	/**
	 * If this {@link ExistingCurseFile} implementation caches the value returned by
	 * {@link #downloadURL()}, this method refreshes this value and returns it.
	 *
	 * @return the refreshed value returned by {@link #downloadURL()}.
	 * @throws CurseException if an error occurs.
	 */
	HttpUrl refreshDownloadURL() throws CurseException;

	/**
	 * Returns this file's changelog.
	 * If this {@link ExistingCurseFile} implementation caches this value,
	 * it may be refreshed by calling {@link #refreshChangelog()}.
	 *
	 * @return an {@link Element} containing this file's changelog. If a changelog is not provided,
	 * an empty {@link Element} is returned.
	 * @throws CurseException if an error occurs.
	 * @see JsoupUtils#emptyElement()
	 */
	Element changelog() throws CurseException;

	/**
	 * Returns this file's changelog as plain text.
	 * If this {@link ExistingCurseFile} implementation caches this value,
	 * it may be refreshed by calling {@link #refreshChangelog()}.
	 *
	 * @return this file's changelog as plain text. If a changelog is not provided, an empty
	 * string is returned.
	 * @throws CurseException if an error occurs.
	 * @see JsoupUtils#getPlainText(Element, int)
	 */
	default String changelogPlainText() throws CurseException {
		return changelogPlainText(Integer.MAX_VALUE);
	}

	/**
	 * Returns this file's changelog as plain text.
	 * If this {@link ExistingCurseFile} implementation caches this value,
	 * it may be refreshed by calling {@link #refreshChangelog()}.
	 *
	 * @param maxLineLength the maximum length of a line. This value is used for word wrapping.
	 * @return this file's changelog as plain text. If a changelog is not provided, an empty
	 * string is returned.
	 * @throws CurseException if an error occurs.
	 * @see JsoupUtils#getPlainText(Element, int)
	 */
	default String changelogPlainText(int maxLineLength) throws CurseException {
		Preconditions.checkArgument(maxLineLength > 0, "maxLineLength should be greater than 0");
		return JsoupUtils.getPlainText(changelog(), maxLineLength).trim();
	}

	/**
	 * If this {@link ExistingCurseFile} implementation caches the value returned by
	 * {@link #changelog()}, this method refreshes this value and returns it.
	 *
	 * @return the refreshed value returned by {@link #changelog()}.
	 * @throws CurseException if an error occurs.
	 */
	Element refreshChangelog() throws CurseException;
}
