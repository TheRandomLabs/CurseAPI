package com.therandomlabs.curseapi.widget;

import java.net.URL;

public final class URLInfo implements Cloneable {
	public URL project;
	public URL curseforge;

	@Override
	public int hashCode() {
		return project.hashCode() * curseforge.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		}

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
