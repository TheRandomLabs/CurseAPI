package com.therandomlabs.curseapi.widget;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import com.therandomlabs.curseapi.game.Game;
import com.therandomlabs.curseapi.util.Utils;

public final class ProjectInfo implements Cloneable {
	public int id;
	public Game game;
	public String type;
	public URLInfo urls;
	public FileInfo[] files;
	public LinkInfo[] links;
	public String title;
	public URL donate;
	public String license;
	public MemberInfo[] members;
	public Map<String, FileInfo[]> versions;
	public DownloadsInfo downloads;
	public URL thumbnail;
	public String[] categories;
	public String created_at;
	public String description;
	public String last_fetch;
	public DownloadInfo download;

	//If an error occurs while retrieving the JSON
	public String error;
	public String message;

	public String json;

	public ProjectInfo() {}

	public ProjectInfo(int id, Game game, String type, URLInfo urls, String title, URL donate,
			String license, MemberInfo[] members, DownloadsInfo downloads, URL thumbnail,
			String createdAt, String description, String lastFetch) {
		this.id = id;
		this.game = game;
		this.type = type;
		this.urls = urls;
		links = new LinkInfo[0];
		this.title = title;
		this.donate = donate;
		this.license = license;
		this.members = members;
		versions = new HashMap<>();
		this.downloads = downloads;
		this.thumbnail = thumbnail;
		this.created_at = createdAt;
		this.description = description;
		this.last_fetch = lastFetch;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		}

		return object instanceof ProjectInfo && ((ProjectInfo) object).id == id;
	}

	@Override
	public ProjectInfo clone() {
		try {
			final ProjectInfo info = (ProjectInfo) super.clone();

			info.urls = urls == null ? null : urls.clone();
			info.files = Utils.tryClone(files);
			info.links = Utils.tryClone(links);
			info.members = Utils.tryClone(members);
			info.versions = Utils.tryClone(versions);
			info.downloads = downloads.clone();
			info.categories = categories.clone();
			info.download = download.clone();

			return info;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	@Override
	public String toString() {
		return "[id=" + id + ",title=\"" + title + "\"]";
	}

	public FileInfo getFileInfo(int id) {
		for(FileInfo[] infos : versions.values()) {
			for(FileInfo info : infos) {
				if(info.id == id) {
					return info;
				}
			}
		}

		return null;
	}
}
