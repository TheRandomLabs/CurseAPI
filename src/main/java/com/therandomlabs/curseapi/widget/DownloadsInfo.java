package com.therandomlabs.curseapi.widget;

public final class DownloadsInfo implements Cloneable {
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
