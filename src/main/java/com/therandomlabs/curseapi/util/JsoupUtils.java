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

package com.therandomlabs.curseapi.util;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;

/**
 * Contains utility methods for working with jsoup.
 */
public final class JsoupUtils {
	private JsoupUtils() {}

	/**
	 * Returns an empty {@link Element}.
	 *
	 * @return an empty {@link Element}.
	 * @see #isEmpty(Element)
	 */
	public static Element emptyElement() {
		return new Element("div");
	}

	/**
	 * Returns whether the specified {@link Element} is {@code null} empty.
	 *
	 * @param element an {@link Element}.
	 * @return {@code true} if the specified {@link Element} is {@code null} or empty,
	 * or otherwise {@code false}.
	 * @see #emptyElement()
	 */
	public static boolean isEmpty(@Nullable Element element) {
		return element == null || element.childNodes().isEmpty();
	}

	/**
	 * Parses the specified HTML fragment and returns the body as a single {@link Element}.
	 * If there are multiple {@link Element}s, they are wrapped in a {@code div} tag.
	 *
	 * @param html an HTML fragment.
	 * @return the body of the specified HTML fragment as a single {@link Element},
	 * or an empty {@link Element} if the body is empty.
	 * @see #emptyElement()
	 */
	public static Element parseBody(String html) {
		final Element body = Jsoup.parseBodyFragment(html).body();
		final Elements children = body.children();

		if (children.isEmpty()) {
			return emptyElement();
		}

		if (children.size() == 1) {
			return children.first();
		}

		return body.tagName("div");
	}

	/**
	 * Converts the specified {@link Element} to plain text.
	 *
	 * @param element an {@link Element}.
	 * @return a plain text representation of the specified {@link Element}.
	 */
	public static String getPlainText(Element element) {
		return getPlainText(element, Integer.MAX_VALUE);
	}

	/**
	 * Converts the specified {@link Element} to plain text.
	 *
	 * @param element an {@link Element}.
	 * @param maxLineLength a maximum line length used for word wrapping.
	 * @return a plain text representation of the specified {@link Element}.
	 */
	public static String getPlainText(Element element, int maxLineLength) {
		Preconditions.checkNotNull(element, "element should not be null");
		Preconditions.checkArgument(maxLineLength > 0, "maxLineLength should be greater than 0");

		final FormattingVisitor formatter = new FormattingVisitor(maxLineLength);
		NodeTraversor.traverse(formatter, element);

		//Some people (e.g. Speiger) do this in their changelogs.
		String string = formatter.toString().replace("\n\n\n", "\n");

		if (string.startsWith("\n")) {
			string = string.substring(1);
		}

		return string.replace("\n", System.lineSeparator());
	}
}
