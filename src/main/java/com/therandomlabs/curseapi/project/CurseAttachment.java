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

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.JsoupUtils;
import com.therandomlabs.curseapi.util.OkHttpUtils;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

/**
 * Represents a CurseForge project attachment image.
 * <p>
 * Implementations of this class should be effectively immutable.
 */
public abstract class CurseAttachment {
	/**
	 * A placeholder {@link CurseAttachment} that represents the placeholder CurseForge logo.
	 */
	public static final CurseAttachment PLACEHOLDER_LOGO = new CurseAttachment() {
		//We can't access the actual link because of CloudFlare:
		//https://www.curseforge.com/Content/2-0-7277-28660/Skins/Elerium/images/icons/
		//avatar-flame.png
		private final HttpUrl url = HttpUrl.get(
				"https://raw.githubusercontent.com/TheRandomLabs/CurseAPI/master/" +
						"placeholder-project-logo.png"
		);

		private final HttpUrl thumbnailURL =
				HttpUrl.get("https://media.forgecdn.net/avatars/0/93/635227964539626926.png");

		private final Element description =
				new Element("p").text("The placeholder CurseForge project logo.");

		@Override
		public int id() {
			return Integer.MAX_VALUE;
		}

		@Override
		public String title() {
			return "Placeholder project logo";
		}

		@Override
		public Element description() {
			return description;
		}

		@Override
		public HttpUrl url() {
			return url;
		}

		@Override
		public HttpUrl thumbnailURL() {
			return thumbnailURL;
		}
	};

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
	 * {@link CurseMember} and the value returned by {@link #id()} is the same for both
	 * {@link CurseMember}s.
	 */
	@Override
	public final boolean equals(Object object) {
		return this == object ||
				(object instanceof CurseAttachment && id() == ((CurseAttachment) object).id());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("id", id()).
				add("title", title()).
				add("url", url()).
				toString();
	}

	/**
	 * Returns this attachment's ID.
	 *
	 * @return this attachment's ID.
	 */
	public abstract int id();

	/**
	 * Returns this attachment's title.
	 *
	 * @return this attachment's title.
	 */
	public abstract String title();

	/**
	 * Returns this attachment's description.
	 *
	 * @return an {@link Element} that contains this attachment's description.
	 * @throws CurseException if an error occurs.
	 */
	public abstract Element description() throws CurseException;

	/**
	 * Returns this attachment's description as plain text.
	 *
	 * @return this attachment's description as plain text.
	 * @throws CurseException if an error occurs.
	 * @see JsoupUtils#getPlainText(Element, int)
	 */
	public String descriptionPlainText() throws CurseException {
		return descriptionPlainText(Integer.MAX_VALUE);
	}

	/**
	 * Returns this attachment's description as plain text.
	 *
	 * @param maxLineLength the maximum length of a line. This value is used for word wrapping.
	 * @return this attachment's description as plain text.
	 * @throws CurseException if an error occurs.
	 * @see JsoupUtils#getPlainText(Element, int)
	 */
	public String descriptionPlainText(int maxLineLength) throws CurseException {
		Preconditions.checkArgument(maxLineLength > 0, "maxLineLength should be greater than 0");
		return JsoupUtils.getPlainText(description(), maxLineLength).trim();
	}

	/**
	 * Returns the URL to this attachment image.
	 *
	 * @return the URL to this attachment image.
	 */
	public abstract HttpUrl url();

	/**
	 * Reads a {@link BufferedImage} from the URL returned by {@link #url()}.
	 *
	 * @return this attachment as a {@link BufferedImage}.
	 * @throws CurseException if an error occurs.
	 */
	public BufferedImage get() throws CurseException {
		return OkHttpUtils.readImage(url());
	}

	/**
	 * Returns the URL to this attachment image's thumbnail.
	 *
	 * @return the URL to this attachment image's thumbnail.
	 */
	public abstract HttpUrl thumbnailURL();

	/**
	 * Reads a {@link BufferedImage} from the URL returned by {@link #thumbnailURL()}.
	 *
	 * @return this attachment image's thumbnail as a {@link BufferedImage}.
	 * @throws CurseException if an error occurs.
	 */
	public BufferedImage thumbnail() throws CurseException {
		return OkHttpUtils.readImage(thumbnailURL());
	}
}
