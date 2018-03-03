package com.therandomlabs.curseapi.project;

import java.net.URL;
import java.time.ZonedDateTime;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.MiscUtils;

public class Relation {
	private final URL url;
	private final String title;
	private final URL authorURL;
	private final String author;
	private final int downloads;
	private final long lastUpdateTime;
	private final String shortDescription;
	private final Category[] categories;

	private final CurseProject relatedFrom;
	private final RelationType relationType;

	Relation(URL url, String title, URL authorURL, String author, int downloads, long lastUpdateTime,
			String shortDescription, Category[] categories, CurseProject relatedFrom,
			RelationType relationType) {
		this.url = url;
		this.title = title;
		this.authorURL = authorURL;
		this.author = author;
		this.downloads = downloads;
		this.lastUpdateTime = lastUpdateTime;
		this.shortDescription = shortDescription;
		this.categories = categories;

		this.relatedFrom = relatedFrom;
		this.relationType = relationType;
	}

	public URL url() {
		return url;
	}

	public String urlString() {
		return url.toString();
	}

	public String title() {
		return title;
	}

	public URL authorURL() {
		return authorURL;
	}

	public String authorURLString() {
		return authorURL.toString();
	}

	public String author() {
		return author;
	}

	public int downloads() {
		return downloads;
	}

	public ZonedDateTime lastUpdateTime() {
		return MiscUtils.parseTime(lastUpdateTime);
	}

	public long lastUpdateTimeSinceEpoch() {
		return lastUpdateTime;
	}

	public String shortDescription() {
		return shortDescription;
	}

	public Category[] categories() {
		return categories;
	}

	public CurseProject asProject() throws CurseException {
		return CurseProject.fromURL(url);
	}

	public CurseProject relatedFrom() {
		return relatedFrom;
	}

	public RelationType relationType() {
		return relationType;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[title=\"" + title + "\",url=\"" + url + "\"]";
	}
}