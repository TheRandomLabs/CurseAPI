package com.therandomlabs.curseapi.project;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseForge;
import com.therandomlabs.curseapi.RelationType;
import com.therandomlabs.curseapi.util.Utils;

public final class Relation {
	private final URL url;
	private final RelationType relationType;
	private final String urlString;
	private final String authorURLString;
	private final URL authorURL;
	private final String title;
	private final String author;
	private final int downloads;
	private final long lastUpdateTimeSinceEpoch;
	private final ZonedDateTime lastUpdateTime;
	private final String shortDescription;
	private final Category[] categories;
	private final CurseProject relatedFrom;

	Relation(URL url, String title, String author, int downloads, long lastUpdateTimeSinceEpoch,
			String shortDescription, Category[] categories, CurseProject relatedFrom,
			RelationType relationType) {
		this.url = url;
		urlString = url.toString();

		authorURLString = CurseForge.URL + "members/" + author;

		URL authorURL = null;

		try {
			authorURL = new URL(authorURLString);
		} catch(MalformedURLException ignored) {}

		this.authorURL = authorURL;

		this.title = title;
		this.author = author;
		this.downloads = downloads;

		this.lastUpdateTimeSinceEpoch = lastUpdateTimeSinceEpoch;
		lastUpdateTime = Utils.parseTime(lastUpdateTimeSinceEpoch);

		this.shortDescription = shortDescription;
		this.categories = categories;

		this.relatedFrom = relatedFrom;
		this.relationType = relationType;
	}

	@Override
	public int hashCode() {
		return urlString.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		}

		return object instanceof Relation && ((Relation) object).urlString.equals(urlString);
	}

	@Override
	public String toString() {
		return "[title=\"" + title + "\",url=\"" + urlString + "\"]";
	}

	public URL url() {
		return url;
	}

	public String urlString() {
		return urlString;
	}

	public String title() {
		return title;
	}

	public String author() {
		return author;
	}

	public URL authorURL() {
		return authorURL;
	}

	public String authorURLString() {
		return authorURLString;
	}

	public int downloads() {
		return downloads;
	}

	public ZonedDateTime lastUpdateTime() {
		return lastUpdateTime;
	}

	public long lastUpdateTimeSinceEpoch() {
		return lastUpdateTimeSinceEpoch;
	}

	public String shortDescription() {
		return shortDescription;
	}

	public Category[] categories() {
		return categories.clone();
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
}
