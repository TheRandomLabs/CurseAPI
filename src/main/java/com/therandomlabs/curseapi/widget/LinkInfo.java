package com.therandomlabs.curseapi.widget;

import java.io.Serializable;

public final class LinkInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = -7219342283246695113L;

	public String href;
	public String title;

	@Override
	public LinkInfo clone() {
		final LinkInfo info = new LinkInfo();

		info.href = href;
		info.title = title;

		return info;
	}

	@Override
	public String toString() {
		return "[href=\"" + href + ",title=\"" + title + "\"]";
	}

	@Override
	public int hashCode() {
		return href.hashCode() + title.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof LinkInfo ? ((LinkInfo) object).hashCode() == hashCode() : false;
	}
}
