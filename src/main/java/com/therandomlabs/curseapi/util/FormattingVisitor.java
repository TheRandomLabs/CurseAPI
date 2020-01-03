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

import java.util.List;

import com.google.common.base.Splitter;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

final class FormattingVisitor implements NodeVisitor {
	private static final Splitter whitespaceSplitter = Splitter.onPattern("\\s+");

	private final int maxLineLength;

	@SuppressWarnings("PMD.AvoidStringBufferField")
	private final StringBuilder text = new StringBuilder();

	private int currentLineLength = 0;
	private boolean shouldGetWholeText;

	FormattingVisitor(int maxLineLength) {
		this.maxLineLength = maxLineLength;
	}

	@Override
	public String toString() {
		return text.toString();
	}

	@Override
	public void head(Node node, int depth) {
		final String name = node.nodeName();

		if ("pre".equals(name)) {
			shouldGetWholeText = true;
		} else if (node instanceof TextNode) {
			final TextNode text = (TextNode) node;

			if (shouldGetWholeText) {
				append(text.getWholeText().trim());
			} else {
				append(text.text());
			}
		} else if ("a".equals(name)) {
			append("[");
		} else if ("li".equals(name)) {
			append("\n * ");
		} else if ("dt".equals(name)) {
			append("  ");
		} else if ("p".equals(name) || "h1".equals(name) || "h2".equals(name) ||
				"h3".equals(name) || "h4".equals(name) || "h5".equals(name) || "tr".equals(name)) {
			append("\n");
		}
	}

	@Override
	public void tail(Node node, int depth) {
		final String name = node.nodeName();

		if ("br".equals(name) || "dd".equals(name) || "dt".equals(name) || "p".equals(name) ||
				"h1".equals(name) || "h2".equals(name) || "h3".equals(name) || "h4".equals(name) ||
				"h5".equals(name)) {
			append("\n");
		} else if ("a".equals(name)) {
			append(String.format("](%s)", node.absUrl("href")));
		} else if ("pre".equals(name)) {
			shouldGetWholeText = false;
		}
	}

	private void append(String string) {
		if (string.startsWith("\n")) {
			//Reset counter if the string starts with a newline.
			currentLineLength = 0;
		}

		if (string.matches("\\s") &&
				(text.length() == 0 || text.substring(text.length() - 1).matches("\\s"))) {
			//Don't accumulate long runs of empty spaces.
			return;
		}

		if (string.length() + currentLineLength <= maxLineLength) {
			text.append(string);
			currentLineLength += string.length();
			return;
		}

		wrapAndAppend(string);
	}

	private void wrapAndAppend(String string) {
		final List<String> words = whitespaceSplitter.splitToList(string);

		for (int i = 0; i < words.size(); i++) {
			final String word = words.get(i);

			if (string.length() + currentLineLength > maxLineLength) {
				//Wrap and reset counter.
				text.append('\n').append(word);
				currentLineLength = word.length();
				continue;
			}

			text.append(word);

			//If this isn't the last word, insert a space.
			if (i < words.size() - 1) {
				text.append(' ');
				currentLineLength++;
			}

			currentLineLength += word.length();
		}
	}
}
