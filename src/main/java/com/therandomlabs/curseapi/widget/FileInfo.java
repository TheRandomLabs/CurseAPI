package com.therandomlabs.curseapi.widget;

import java.net.URL;
import com.therandomlabs.curseapi.ReleaseType;

public final class FileInfo implements Cloneable {
	public int id;
	public URL url;
	public String name;
	public ReleaseType type;
	public String version;
	public String filesize;
	public String[] versions;
	public int downloads;
	public String uploaded_at;

	public FileInfo() {}

	public FileInfo(int id, URL url, String name, ReleaseType type,
			String[] versions, String filesize, int downloads, String uploadedAt) {
		this.id = id;
		this.url = url;
		this.name = name;
		this.type = type;
		version = versions[0];
		this.filesize = filesize;
		this.versions = versions;
		this.downloads = downloads;
		uploaded_at = uploadedAt;
	}

	@Override
	public FileInfo clone() {
		final FileInfo info = new FileInfo();

		info.id = id;
		info.url = url;
		info.name = name;
		info.type = type;
		info.version = version;
		info.filesize = filesize;
		info.versions = versions.clone();
		info.uploaded_at = uploaded_at;

		return info;
	}
}
