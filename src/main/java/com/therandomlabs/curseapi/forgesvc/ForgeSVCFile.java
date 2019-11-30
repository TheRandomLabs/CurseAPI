package com.therandomlabs.curseapi.forgesvc;

import java.time.ZonedDateTime;

import com.therandomlabs.curseapi.file.CurseFile;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

final class ForgeSVCFile implements CurseFile {
	private int projectId;
	private int id;
	private String displayName;
	private String fileName;
	private ZonedDateTime fileDate;
	private long fileLength;
	private HttpUrl downloadUrl;
	private Element changelog;

	@Override
	public int projectID() {
		return projectId;
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public String displayName() {
		return displayName;
	}

	@Override
	public String fileName() {
		return fileName;
	}

	@Override
	public ZonedDateTime uploadTime() {
		return fileDate;
	}

	@Override
	public long fileSize() {
		return fileLength;
	}

	@Override
	public HttpUrl downloadURL() {
		return downloadUrl;
	}

	@Override
	public Element changelog() {
		return changelog;
	}
}
