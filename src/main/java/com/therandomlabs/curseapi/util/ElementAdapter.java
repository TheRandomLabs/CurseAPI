package com.therandomlabs.curseapi.util;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public final class ElementAdapter {
	public static final ElementAdapter INSTANCE = new ElementAdapter();

	private ElementAdapter() {}

	@ToJson
	public String toJson(Element element) {
		return element.outerHtml();
	}

	@FromJson
	public Element fromJson(String element) {
		return Jsoup.parse(element);
	}
}
