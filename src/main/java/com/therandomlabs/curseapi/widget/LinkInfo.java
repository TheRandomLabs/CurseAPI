package com.therandomlabs.curseapi.widget;

import java.io.Serializable;

public final class LinkInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = -7219342283246695113L;

	public String href;
	public String title;

	@Override
	public int hashCode() {
		return href.hashCode() + title.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof LinkInfo && object.hashCode() == hashCode();
	}

	@Override
	public LinkInfo clone() {
		try {
			final LinkInfo info = (LinkInfo) super.clone();

			info.href = href;
			info.title = title;

			return info;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	@Override
	public String toString() {
		return "[href=\"" + href + ",title=\"" + title + "\"]";
	}
}
