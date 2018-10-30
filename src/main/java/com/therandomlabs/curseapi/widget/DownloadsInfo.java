package com.therandomlabs.curseapi.widget;

public final class DownloadsInfo implements Cloneable {
	public int total;
	public int monthly;

	@Override
	public int hashCode() {
		return total + monthly;
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		}

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
