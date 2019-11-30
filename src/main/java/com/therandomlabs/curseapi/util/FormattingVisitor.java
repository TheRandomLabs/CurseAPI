package com.therandomlabs.curseapi.util;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

final class FormattingVisitor implements NodeVisitor {
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
		final String[] words = string.split("\\s+");

		for (int i = 0; i < words.length; i++) {
			final String word = words[i];

			if (string.length() + currentLineLength > maxLineLength) {
				//Wrap and reset counter.
				text.append('\n').append(word);
				currentLineLength = word.length();
				continue;
			}

			text.append(word);

			//If this isn't the last word, insert a space.
			if (i < words.length - 1) {
				text.append(' ');
				currentLineLength++;
			}

			currentLineLength += word.length();
		}
	}
}
