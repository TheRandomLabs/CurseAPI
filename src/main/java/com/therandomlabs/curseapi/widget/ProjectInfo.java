package com.therandomlabs.curseapi.widget;

import java.net.URL;
import java.util.HashMap;
import com.therandomlabs.curseapi.Game;
import com.therandomlabs.curseapi.util.CloneException;

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
	public ProjectInfo clone() {
		final ProjectInfo info = new ProjectInfo();

		info.id = id;
		info.game = game;
		info.type = type;
		info.urls = urls == null ? null : urls.clone();
		info.files = CloneException.tryClone(files);
		info.links = CloneException.tryClone(links);
		info.title = title;
		info.donate = donate;
		info.license = license;
		info.members = CloneException.tryClone(members);
		info.versions = CloneException.tryClone(versions);
		info.downloads = downloads.clone();
		info.thumbnail = thumbnail;
		info.categories = categories.clone();
		info.created_at = created_at;
		info.description = description;
		info.last_fetch = last_fetch;
		info.download = download.clone();

		info.error = error;
		info.message = message;

		info.json = json;

		return info;
	}
}
