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

package com.therandomlabs.curseapi.project;

import java.awt.image.BufferedImage;
import java.time.ZonedDateTime;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.util.JsoupUtils;
import com.therandomlabs.curseapi.util.OkHttpUtils;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

/**
 * Represents a CurseForge project.
 * <p>
 * Implementations of this class should be effectively immutable.
 */
public abstract class CurseProject implements Comparable<CurseProject> {
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
	 * {@link CurseProject} and the value returned by {@link #id()} is the same for both
	 * {@link CurseProject}s.
	 */
	@Override
	public final boolean equals(Object object) {
		return this == object ||
				(object instanceof CurseProject && id() == ((CurseProject) object).id());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("id", id()).
				add("name", name()).
				add("url", url()).
				toString();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * {@link String#compareTo(String)} is used on the values returned by
	 * {@link #name()} to determine the value that this method returns.
	 */
	@Override
	public final int compareTo(CurseProject project) {
		return name().compareTo(project.name());
	}

	/**
	 * Returns this project's ID.
	 *
	 * @return this project's ID.
	 */
	public abstract int id();

	/**
	 * Returns this project's name.
	 *
	 * @return this project's name.
	 */
	public abstract String name();

	/**
	 * Returns this project's main author.
	 *
	 * @return this project's main author.
	 */
	public abstract CurseMember author();

	/**
	 * Returns this project's authors.
	 *
	 * @return a mutable {@link Set} that contains this project's authors.
	 */
	public abstract Set<CurseMember> authors();

	/**
	 * Returns the URL to this project's logo image.
	 *
	 * @return the URL to this project's logo image.
	 */
	public abstract HttpUrl logoURL();

	/**
	 * Reads a {@link BufferedImage} from the URL returned by {@link #logoURL()}.
	 *
	 * @return this project's logo as a {@link BufferedImage}.
	 * @throws CurseException if an error occurs.
	 */
	public BufferedImage logo() throws CurseException {
		return OkHttpUtils.readImage(logoURL());
	}

	/**
	 * Returns the URL to this project's logo thumbnail.
	 *
	 * @return the URL to this project's logo thumbnail.
	 */
	public abstract HttpUrl logoThumbnailURL();

	/**
	 * Reads a {@link BufferedImage} from the URL returned by {@link #logoThumbnailURL()}.
	 *
	 * @return this project's logo thumbnail as a {@link BufferedImage}.
	 * @throws CurseException if an error occurs.
	 */
	public BufferedImage logoThumbnail() throws CurseException {
		return OkHttpUtils.readImage(logoThumbnailURL());
	}

	/**
	 * Returns this project's URL.
	 *
	 * @return this project's URL.
	 */
	public abstract HttpUrl url();

	/**
	 * Returns the ID of this project's game.
	 *
	 * @return the ID of this project's game.
	 */
	public abstract int gameID();

	/**
	 * Returns this project's game.
	 * This value may be refreshed by calling {@link #clearGameCache()}.
	 *
	 * @return a {@link CurseGame} instance that represents this project's game.
	 * @throws CurseException if an error occurs.
	 */
	public abstract CurseGame game() throws CurseException;

	/**
	 * If this {@link CurseProject} implementation caches the value returned by
	 * {@link #game()} and supports clearing this cache, this method clears this cached value.
	 */
	public abstract void clearGameCache();

	/**
	 * Returns this project's summary.
	 *
	 * @return this project's summary.
	 */
	public abstract String summary();

	/**
	 * Returns this project's description.
	 *
	 * @return an {@link Element} that contains this project's description.
	 * @throws CurseException if an error occurs.
	 */
	public abstract Element description() throws CurseException;

	/**
	 * Returns this project's description as plain text.
	 *
	 * @return this project's description as plain text.
	 * @throws CurseException if an error occurs.
	 * @see JsoupUtils#getPlainText(Element, int)
	 */
	public String descriptionPlainText() throws CurseException {
		return descriptionPlainText(Integer.MAX_VALUE);
	}

	/**
	 * Returns this project's description as plain text.
	 *
	 * @param maxLineLength the maximum length of a line. This value is used for word wrapping.
	 * @return this project's description as plain text.
	 * @throws CurseException if an error occurs.
	 * @see JsoupUtils#getPlainText(Element, int)
	 */
	public String descriptionPlainText(int maxLineLength) throws CurseException {
		Preconditions.checkArgument(maxLineLength > 0, "maxLineLength should be greater than 0");
		return JsoupUtils.getPlainText(description(), maxLineLength).trim();
	}

	/**
	 * If this {@link CurseProject} implementation caches the value returned by
	 * {@link #description()} and supports clearing this cache, this method clears this cached
	 * value.
	 */
	public abstract void clearDescriptionCache();

	/**
	 * Returns this project's download count.
	 *
	 * @return this project's download count.
	 */
	public abstract int downloadCount();

	/**
	 * Returns a {@link CurseFiles} instance for this project.
	 * This value may be refreshed by calling {@link #clearFilesCache()}.
	 *
	 * @return a {@link CurseFiles} instance for this project.
	 * @throws CurseException if an error occurs.
	 */
	public abstract CurseFiles<CurseFile> files() throws CurseException;

	/**
	 * If this {@link CurseProject} implementation caches the value returned by
	 * {@link #files()} and supports clearing this cache, this method clears this cached value.
	 */
	public abstract void clearFilesCache();

	/**
	 * Returns the URL of the file in this project with the specified ID.
	 * The existence and availability of the file are not verified.
	 * <p>
	 * Implementations of this method should validate the file ID by calling
	 * {@link com.therandomlabs.curseapi.CursePreconditions#checkFileID(int, String)}.
	 *
	 * @param fileID a file ID.
	 * @return the URL of the file in this project with the specified ID.
	 */
	public abstract HttpUrl fileURL(int fileID);

	/**
	 * Returns this project's primary category.
	 *
	 * @return this project's primary category.
	 */
	public abstract CurseCategory primaryCategory();

	/**
	 * Returns this project's categories.
	 *
	 * @return a mutable {@link Set} that contains this project's categories.
	 */
	public abstract Set<CurseCategory> categories();

	/**
	 * Returns this project's category section.
	 *
	 * @return this project's category section.
	 */
	public abstract CurseCategorySection categorySection();

	/**
	 * Returns this project's slug.
	 *
	 * @return this project's slug.
	 */
	public abstract String slug();

	/**
	 * Returns this project's creation time.
	 *
	 * @return a {@link ZonedDateTime} instance that represents this project's creation time.
	 */
	public abstract ZonedDateTime creationTime();

	/**
	 * Returns this project's last update time.
	 *
	 * @return a {@link ZonedDateTime} instance that represents this project's last update time.
	 */
	public abstract ZonedDateTime lastUpdateTime();

	/**
	 * Returns this project's last modification time.
	 *
	 * @return a {@link ZonedDateTime} instance that represents this project's last modification
	 * time.
	 */
	public abstract ZonedDateTime lastModificationTime();

	/**
	 * Returns whether this project is experimental.
	 *
	 * @return {@code true} if this project is experimental, or otherwise {@code false}.
	 */
	public abstract boolean experimental();
}
