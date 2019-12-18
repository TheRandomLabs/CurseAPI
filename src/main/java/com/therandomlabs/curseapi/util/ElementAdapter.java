package com.therandomlabs.curseapi.util;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 * A Moshi adapter for {@link Element}s.
 */
public final class ElementAdapter {
	/**
	 * The singleton instance of {@link ElementAdapter}.
	 */
	public static final ElementAdapter INSTANCE = new ElementAdapter();

	private ElementAdapter() {}

	/**
	 * Converts the specified {@link Element} to a JSON string.
	 *
	 * @param element an {@link Element}.
	 * @return a JSON string representation of the specified {@link Element}.
	 */
	@ToJson
	public String toJSON(Element element) {
		return element.outerHtml();
	}

	/**
	 * Converts the specified JSON string to an {@link Element}.
	 *
	 * @param element a JSON string.
	 * @return an {@link Element}.
	 */
	@FromJson
	public Element fromJSON(String element) {
		return Jsoup.parse(element);
	}
}
