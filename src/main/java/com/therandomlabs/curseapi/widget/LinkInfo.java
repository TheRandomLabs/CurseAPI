package com.therandomlabs.curseapi.widget;

public final class LinkInfo implements Cloneable {
	public String href;
	public String title;

	@Override
	public int hashCode() {
		return href.hashCode() + title.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		}

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
