package com.therandomlabs.curseapi.widget;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import com.therandomlabs.curseapi.Game;
import com.therandomlabs.curseapi.util.CloneException;

public final class ProjectInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = -7608267242380681184L;

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
	public HashMap<String, FileInfo[]> versions;
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

	public boolean retrievedDirectly = true;
	public boolean failedToRetrieveDirectly;

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

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof ProjectInfo && ((ProjectInfo) object).id == id;
	}

	@Override
	public ProjectInfo clone() {
		try {
			final ProjectInfo info = (ProjectInfo) super.clone();

			info.urls = urls == null ? null : urls.clone();
			info.files = CloneException.tryClone(files);
			info.links = CloneException.tryClone(links);
			info.members = CloneException.tryClone(members);
			info.versions = CloneException.tryClone(versions);
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
}
