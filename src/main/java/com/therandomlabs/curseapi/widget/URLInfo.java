package com.therandomlabs.curseapi.widget;

import java.io.Serializable;
import java.net.URL;

public final class URLInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = 8414622257848610580L;

	public URL project;
	public URL curseforge;

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

	@Override
	public int hashCode() {
		return project.hashCode() + curseforge.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof URLInfo && object.hashCode() == hashCode();
	}
}
