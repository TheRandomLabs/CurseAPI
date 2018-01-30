package com.therandomlabs.curseapi;

import java.net.URL;

public class CategoryInfo implements Cloneable {
	public String name;
	public URL url;
	public URL thumbnailURL;

	public CategoryInfo() {}

	public CategoryInfo(String name, URL url, URL thumbnailURL) {
		this.name = name;
		this.url = url;
		this.thumbnailURL = thumbnailURL;
	}

	@Override
	public CategoryInfo clone() {
		return new CategoryInfo(name, url, thumbnailURL);
	}

	@Override
	public String toString() {
		return getClass().getName() + "[name=\"" + name + "\",url=\"" + url + "\",thumbnailURL=\"" +
				thumbnailURL + "\"]";
	}
}
