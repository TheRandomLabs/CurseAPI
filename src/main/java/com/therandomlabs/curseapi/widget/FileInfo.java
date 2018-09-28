package com.therandomlabs.curseapi.widget;

import java.net.URL;
import com.therandomlabs.curseapi.file.ReleaseType;

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

	public FileInfo(int id, URL url, String name, ReleaseType type, String[] versions,
			String filesize, int downloads, String uploadedAt) {
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
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof FileInfo && ((FileInfo) object).id == id;
	}

	@Override
	public FileInfo clone() {
		try {
			final FileInfo info = (FileInfo) super.clone();
			info.versions = versions.clone();
			return info;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	@Override
	public String toString() {
		return "[id=" + id + ",name=\"" + name + "\"]";
	}
}
