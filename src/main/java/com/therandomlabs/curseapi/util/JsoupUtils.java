package com.therandomlabs.curseapi.util;

import com.google.common.base.Preconditions;
import org.jsoup.nodes.Element;
import org.jsoup.select.NodeTraversor;

public final class JsoupUtils {
	private JsoupUtils() {}

	public static String getPlainText(Element element) {
		return getPlainText(element, Integer.MAX_VALUE);
	}

	public static String getPlainText(Element element, int maxLineLength) {
		Preconditions.checkNotNull(element, "element should not be null");
		Preconditions.checkArgument(maxLineLength > 0, "maxLineLength should be greater than 0");

		final FormattingVisitor formatter = new FormattingVisitor(maxLineLength);
		NodeTraversor.traverse(formatter, element);

		//Some people (e.g. Speiger) do this in their changelogs.
		final String string = formatter.toString().replaceAll("\n\n\n", "\n");
		return string.startsWith("\n") ? string.substring(1) : string;
	}
}
