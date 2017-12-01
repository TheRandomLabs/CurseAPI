package com.therandomlabs.curseapi.widget;

public final class LinkInfo implements Cloneable {
	public String href;
	public String title;

	@Override
	public LinkInfo clone() {
		final LinkInfo info = new LinkInfo();

		info.href = href;
		info.title = title;

		return info;
	}
}
