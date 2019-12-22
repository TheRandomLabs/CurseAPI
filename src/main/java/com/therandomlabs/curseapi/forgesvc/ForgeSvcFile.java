package com.therandomlabs.curseapi.forgesvc;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseDependency;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFileStatus;
import com.therandomlabs.curseapi.file.CurseReleaseType;
import com.therandomlabs.curseapi.game.CurseGameVersion;
import com.therandomlabs.curseapi.project.CurseProject;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

final class ForgeSvcFile extends CurseFile {
	private int projectId;
	private int id;
	private String displayName;
	private String fileName;
	private ZonedDateTime fileDate;
	private long fileLength;
	private int releaseType;
	private int fileStatus;
	private HttpUrl downloadUrl;
	private Set<ForgeSvcDependency> dependencies;
	private Set<String> gameVersion;

	private transient boolean dependenciesInitialized;

	//Cache.
	private transient CurseProject project;
	private transient SortedSet<CurseGameVersion<?>> gameVersions;
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
		if (!dependenciesInitialized) {
			for (ForgeSvcDependency dependency : dependencies) {
				dependency.setDependent(this);
			}

			dependenciesInitialized = true;
		}

		return new HashSet<>(dependencies);
	}

	@Override
	public Set<String> gameVersionStrings() {
		return new LinkedHashSet<>(gameVersion);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V extends CurseGameVersion<?>> SortedSet<V> gameVersions() throws CurseException {
		if (gameVersions == null) {
			final Set<String> versionStrings = gameVersionStrings();
			gameVersions = new TreeSet<>();
			final int gameID = project().gameID();

			for (String versionString : versionStrings) {
				CurseAPI.<V>gameVersion(gameID, versionString).ifPresent(gameVersions::add);
			}
		}

		return (SortedSet<V>) gameVersions;
	}

	@Override
	public void clearGameVersionsCache() {
		gameVersions = null;
	}

	@Override
	public Element changelog() throws CurseException {
		if (changelog == null) {
			final Optional<Element> optionalChangelog = CurseAPI.fileChangelog(projectId, id);

			if (!optionalChangelog.isPresent()) {
				throw new CurseException("Failed to retrieve changelog for file: " + this);
			}

			changelog = optionalChangelog.get();
		}

		return changelog;
	}

	@Override
	public void clearChangelogCache() {
		changelog = null;
	}

	//This is called by ForgeSvcProvider so that projectId is not 0.
	void setProjectID(int id) {
		projectId = id;
	}

	//This is called by ForgeSvcProject#files().
	void setProject(CurseProject project) {
		this.project = project;
	}
}
