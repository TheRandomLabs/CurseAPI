package com.therandomlabs.curseapi.widget;

import java.net.URL;

public final class URLInfo implements Cloneable {
	public URL project;
	public URL curseforge;

	@Override
	public int hashCode() {
		return project.toString().hashCode() + curseforge.toString().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof URLInfo && object.hashCode() == hashCode();
	}

	@Override
	public URLInfo clone() {
		try {
			return (URLInfo) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	@Override
	public String toString() {
		return "[project=\"" + project + "\",curseforge=\"" + curseforge + "\"]";
	}
}
