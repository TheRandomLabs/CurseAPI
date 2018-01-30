package com.therandomlabs.curseapi;

import java.net.URL;
import com.therandomlabs.curseapi.util.CloneException;

public class RelationInfo implements Cloneable {
	public URL url;
	public String title;
	public URL authorURL;
	public String author;
	public int downloads;
	public int lastUpdateTime;
	public String description;
	public CategoryInfo[] categories;

	public CurseProject toProject() throws CurseException {
		return CurseProject.fromURL(url);
	}

	@Override
	public RelationInfo clone() {
		final RelationInfo info = new RelationInfo();

		info.url = url;
		info.title = title;
		info.authorURL = authorURL;
		info.author = author;
		info.downloads = downloads;
		info.lastUpdateTime = lastUpdateTime;
		info.description = description;
		info.categories = CloneException.tryClone(categories);

		return info;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[title=\"" + title + "\",url=\"" + url + "\"]";
	}
}
