package com.therandomlabs.curseapi.project;

import java.net.URL;
import java.time.ZonedDateTime;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.MiscUtils;

public class Relation {
	URL url;
	String title;
	URL authorURL;
	String author;
	int downloads;
	long lastUpdateTime;
	String description;
	Category[] categories;

	Relation() {}

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

	public String description() {
		return description;
	}

	public Category[] categories() {
		return categories;
	}

	public CurseProject asProject() throws CurseException {
		return CurseProject.fromURL(url);
	}

	@Override
	public String toString() {
		return getClass().getName() + "[title=\"" + title + "\",url=\"" + url + "\"]";
	}
}
