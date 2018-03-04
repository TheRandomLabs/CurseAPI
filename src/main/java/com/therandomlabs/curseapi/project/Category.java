package com.therandomlabs.curseapi.project;

import java.io.Serializable;
import java.net.URL;

public class Category implements Serializable {
	private static final long serialVersionUID = 4578392820713062750L;

	private String name;
	private URL url;
	private URL thumbnailURL;

	Category(String name, URL url, URL thumbnailURL) {
		this.name = name;
		this.url = url;
		this.thumbnailURL = thumbnailURL;
	}

	public String name() {
		return name;
	}

	public URL url() {
		return url;
	}

	public String urlString() {
		return url.toString();
	}

	public URL thumbnailURL() {
		return thumbnailURL;
	}

	public String thumbnailURLString() {
		return thumbnailURL.toString();
	}

	@Override
	public String toString() {
		return "[name=\"" + name + "\",url=\"" + url + "\",thumbnailURL=\"" + thumbnailURL + "\"]";
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof Category ? ((Category) object).url.equals(url) : false;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}
}
