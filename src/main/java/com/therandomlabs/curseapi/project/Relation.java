package com.therandomlabs.curseapi.project;

import java.net.URL;
import java.time.ZonedDateTime;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseForge;
import com.therandomlabs.curseapi.util.MiscUtils;
import com.therandomlabs.curseapi.util.URLUtils;

public final class Relation {
	private final URL url;
	private final String title;
	private final String author;
	private final int downloads;
	private final long lastUpdateTime;
	private final String shortDescription;
	private final Category[] categories;

	private final CurseProject relatedFrom;
	private final RelationType relationType;

	Relation(URL url, String title, String author, int downloads,
			long lastUpdateTime, String shortDescription, Category[] categories,
			CurseProject relatedFrom, RelationType relationType) {
		this.url = url;
		this.title = title;
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

	public URL authorURL() throws CurseException {
		return URLUtils.url(authorURLString());
	}

	public String authorURLString() {
		return CurseForge.URL + "members/" + author;
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
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof Relation && ((Relation) object).url.equals(url);
	}

	@Override
	public String toString() {
		return "[title=\"" + title + "\",url=\"" + url + "\"]";
	}
}
