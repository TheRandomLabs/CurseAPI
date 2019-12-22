package com.therandomlabs.curseapi.util;

import com.google.common.base.Preconditions;
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
	 * Parses the specified HTML fragment and returns the body as a single {@link Element}.
	 *
	 * @param html an HTML fragment.
	 * @return the body of the specified HTML fragment as a single {@link Element},
	 * or {@code null} if the body is empty.
	 */
	public static Element parseBody(String html) {
		final Element body = Jsoup.parseBodyFragment(html).body();
		final Elements children = body.children();

		if (children.size() == 0) {
			return null;
		}

		if (children.size() == 1) {
			return children.first();
		}

		final Element div = new Element("div");

		for (Element child : children) {
			child.appendTo(div);
		}

		return div;
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
		final String string = formatter.toString().replace("\n\n\n", "\n");
		return string.startsWith("\n") ? string.substring(1) : string;
	}
}
