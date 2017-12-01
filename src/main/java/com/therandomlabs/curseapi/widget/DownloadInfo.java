package com.therandomlabs.curseapi.widget;

import com.therandomlabs.curseapi.ReleaseType;

public final class DownloadInfo implements Cloneable {
	public int id;
	public String url;
	public String name;
	public ReleaseType type;
	public String version;
	public String filesize;
	public String[] versions;
	public int downloads;
	public DateInfo uploaded_at;

	@Override
	public DownloadInfo clone() {
		final DownloadInfo info = new DownloadInfo();

		info.id = id;
		info.url = url;
		info.name = name;
		info.type = type;
		info.version = version;
		info.filesize = filesize;
		info.versions = versions.clone();
		info.uploaded_at = uploaded_at.clone();

		return info;
	}
}
