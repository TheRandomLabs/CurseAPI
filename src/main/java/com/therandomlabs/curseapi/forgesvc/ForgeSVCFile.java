package com.therandomlabs.curseapi.forgesvc;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseDependency;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFileStatus;
import com.therandomlabs.curseapi.file.CurseReleaseType;
import com.therandomlabs.curseapi.project.CurseProject;
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
	private int releaseType;
	private int fileStatus;
	private HttpUrl downloadUrl;
	private Set<ForgeSVCDependency> dependencies;
	private Set<String> gameVersion;

	//Cache.
	private transient CurseProject project;
	private transient Element changelog;

	@Override
	public int projectID() {
		return projectId;
	}

	@Override
	public CurseProject project() throws CurseException {
		if (project == null) {
			project = CurseAPI.project(projectId).orElse(null);
		}

		return project;
	}

	@Override
	public void clearProjectCache() {
		project = null;
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
	public String nameOnDisk() {
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
	public CurseReleaseType releaseType() {
		return CurseReleaseType.fromID(releaseType);
	}

	@Override
	public CurseFileStatus status() {
		return CurseFileStatus.fromID(fileStatus);
	}

	@Override
	public HttpUrl downloadURL() {
		return downloadUrl;
	}

	@Override
	public Set<CurseDependency> dependencies() {
		return new HashSet<>(dependencies);
	}

	@Override
	public Set<String> gameVersions() {
		return new LinkedHashSet<>(gameVersion);
	}

	@Override
	public Element changelog() throws CurseException {
		if (changelog == null) {
			changelog = RetrofitUtils.getElement(
					ForgeSVCProvider.FORGESVC.getChangelog(projectId, id)
			);
		}

		return changelog;
	}

	@Override
	public void clearChangelogCache() {
		changelog = null;
	}

	//This is used by ForgeSVCProvider so that projectId is not 0.
	void setProjectID(int id) {
		projectId = id;
	}
}
