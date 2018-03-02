package com.therandomlabs.curseapi.widget;

import java.io.Serializable;

public final class DownloadsInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = 8892506591321439267L;

	public int total;
	public int monthly;

	@Override
	public DownloadsInfo clone() {
		final DownloadsInfo info = new DownloadsInfo();

		info.total = total;
		info.monthly = monthly;

		return info;
	}
}
