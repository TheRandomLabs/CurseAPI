package com.therandomlabs.curseapi.widget;

import java.io.Serializable;

public final class DownloadsInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = 8892506591321439267L;

	public int total;
	public int monthly;

	@Override
	public int hashCode() {
		return total + monthly;
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof DownloadsInfo && object.hashCode() == hashCode();
	}

	@Override
	public DownloadsInfo clone() {
		try {
			return (DownloadsInfo) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	@Override
	public String toString() {
		return "[total=" + total + ",monthly=" + monthly + "]";
	}
}
