package com.therandomlabs.curseapi.forgesvc;

import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseFile;
import com.therandomlabs.curseapi.util.RetrofitUtils;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

final class ForgeSVCFile extends CurseFile {
	private int projectId;
	private int id;
	private String displayName;
	private String fileName;
	private ZonedDateTime fileDate;
	private long fileLength;
	private HttpUrl downloadUrl;
	private Set<String> gameVersions;

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
	public Set<String> gameVersions() {
		return new LinkedHashSet<>(gameVersions);
	}

	@Override
	public Element changelog() throws CurseException {
		return RetrofitUtils.getElement(ForgeSVCProvider.FORGESVC.getChangelog(projectId, id));
	}
}
