package com.therandomlabs.curseapi.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.curseapi.cursemeta.AddOnFile;
import com.therandomlabs.curseapi.cursemeta.AddOnFileDependency;
import com.therandomlabs.curseapi.cursemeta.CurseMeta;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.MiscUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.curseapi.widget.FileInfo;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.TRLCollectors;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.concurrent.ThreadUtils;
import com.therandomlabs.utils.io.NIOUtils;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.network.NetworkUtils;
import org.jsoup.nodes.Element;

//TODO Additional Files
//TODO get dependencies by type
public final class CurseFile {
	private final int projectID;
	private CurseProject project;
	private final FileStatus status;
	private URL url;
	private final int id;
	private final String name;
	private final String nameOnDisk;
	private final URL downloadURL;
	private final String downloadURLString;
	private final ReleaseType releaseType;
	private final ZonedDateTime uploadTime;
	private final String fileSize;
	private final int downloads;
	private final String md5;
	private final TRLList<Integer> dependencyIDs;
	private TRLList<CurseProject> dependencies;
	private final TRLList<String> gameVersions;
	private final TRLList<MinecraftVersion> minecraftVersions;
	private Element changelogHTML;
	private String changelog;
	private final boolean curseMeta;

	public CurseFile(int projectID, AddOnFile info) throws CurseException {
		this(projectID, null, info.FileStatus, info.Id, info.FileName, info.FileNameOnDisk,
				info.DownloadURL, info.releaseType(), info.FileDate, null, -1,
				getDependencyIDs(info.Dependencies), info.GameVersion, true);
	}

	public CurseFile(CurseProject project, AddOnFile info) throws CurseException {
		this(project.id(), project, info.FileStatus, info.Id, info.FileName, info.FileNameOnDisk,
				info.DownloadURL, info.releaseType(), info.FileDate, null, -1,
				getDependencyIDs(info.Dependencies), info.GameVersion, true);
	}

	public CurseFile(int projectID, FileInfo info) throws CurseException {
		this(projectID, null, FileStatus.NORMAL, info.id, info.name, null, null, info.type,
				info.uploaded_at, info.filesize, info.downloads, null, info.versions, false);
	}

	public CurseFile(CurseProject project, FileInfo info) throws CurseException {
		this(project.id(), project, FileStatus.NORMAL, info.id, info.name, null, null, info.type,
				info.uploaded_at, info.filesize, info.downloads, null, info.versions, false);
	}

	public CurseFile(int projectID, CurseProject project, FileStatus status, int id, String name,
			String nameOnDisk, URL downloadURL, ReleaseType releaseType, String uploadTime,
			String fileSize, int downloads, TRLList<Integer> dependencyIDs,
			String[] gameVersions, boolean curseMeta) throws CurseException {
		this.projectID = projectID;
		this.project = project;
		this.status = status;

		if(project != null) {
			url = URLUtils.url(project.urlString() + "/files/" + id);
		}

		this.id = id;
		this.name = name;
		this.nameOnDisk = nameOnDisk == null ?
				DocumentUtils.getValue(url, "class=details-info;class=info-data;text") : nameOnDisk;
		this.downloadURL = downloadURL == null ?
				CurseForge.getFileURL(project.id(), id) : downloadURL;
		downloadURLString = this.downloadURL.toString();
		this.releaseType = releaseType;
		this.uploadTime = MiscUtils.parseTime(uploadTime);
		this.fileSize = fileSize;
		this.downloads = downloads;
		//TODO get with CurseMeta
		this.md5 = curseMeta ? null : DocumentUtils.getValue(url, "class=md5;text");
		this.dependencyIDs =
				dependencyIDs == null ? getDependencies(url) : dependencyIDs.toImmutableList();

		final TRLList<String> gameVersionList = new TRLList<>();
		for(String gameVersion : gameVersions) {
			if(!gameVersion.startsWith("Java ")) {
				gameVersionList.add(gameVersion);
			}
		}
		this.gameVersions = gameVersionList.toImmutableList();

		final TRLList<MinecraftVersion> minecraftVersions =
					CollectionUtils.convert(this.gameVersions, MinecraftVersion::fromString);
		minecraftVersions.removeIf(Objects::isNull);
		minecraftVersions.sort();
		this.minecraftVersions = minecraftVersions.toImmutableList();

		this.curseMeta = curseMeta;
	}

	public FileStatus status() {
		return status;
	}

	public int id() {
		return id;
	}

	public String name() {
		return name;
	}

	public String nameOnDisk() {
		return nameOnDisk;
	}

	public URL url() throws CurseException {
		if(url == null) {
			url = URLUtils.url(CurseForge.fromID(projectID) + "/files/" + id);
		}

		return url;
	}

	public String urlString() {
		return url.toString();
	}

	public URL downloadURL() {
		return downloadURL;
	}

	public String downloadURLString()  {
		return downloadURLString;
	}

	public ReleaseType releaseType() {
		return releaseType;
	}

	public TRLList<Integer> dependencyIDs() {
		return dependencyIDs;
	}

	public TRLList<CurseProject> dependencies() throws CurseException {
		if(dependencies == null) {
			final TRLList<CurseProject> dependencyList = new TRLList<>(dependencyIDs.size());
			ThreadUtils.splitWorkload(CurseAPI.getMaximumThreads(), dependencyIDs.size(),
					index -> dependencyList.add(CurseProject.fromID(dependencyIDs.get(index))));
			dependencies = dependencyList.toImmutableList();
		}

		return dependencies;
	}

	public String gameVersion() {
		return gameVersions.get(0);
	}

	public TRLList<String> gameVersions() {
		return gameVersions;
	}

	public MinecraftVersion minecraftVersion() {
		return minecraftVersions.get(0);
	}

	public TRLList<MinecraftVersion> minecraftVersions() {
		return minecraftVersions;
	}

	public ZonedDateTime uploadTime() {
		return uploadTime;
	}

	public String fileSize() {
		return fileSize;
	}

	public int downloads() {
		return downloads;
	}

	public String md5() {
		return md5;
	}

	public boolean hasChangelog() throws CurseException {
		String changelog = changelog().trim().toLowerCase(Locale.ENGLISH);
		if(StringUtils.lastChar(changelog) == '.') {
			changelog = StringUtils.removeLastChar(changelog);
		}
		return !changelog.equals("no changelog provided") && !changelog.equals("n/a");
	}

	public String changelog() throws CurseException {
		changelogHTML();
		return changelog;
	}

	public Element changelogHTML() throws CurseException {
		if(changelogHTML == null) {
			if(curseMeta) {
				changelogHTML = CurseMeta.getChangelog(project.id(), id);
			} else {
				changelogHTML = DocumentUtils.get(url, "class=logbox");
			}

			changelog = DocumentUtils.getPlainText(changelogHTML);
		}

		return changelogHTML;
	}

	public String uploader() throws CurseException {
		return DocumentUtils.getValue(url, "class=user-tag;tag=a=1;text");
	}

	public URL uploaderURL() throws CurseException {
		return URLUtils.url(DocumentUtils.getValue(url, "class=user-tag;tag=a=1;absUrl=href"));
	}

	public int projectID() {
		return projectID;
	}

	public CurseProject project() throws CurseException {
		if(project == null) {
			project = CurseProject.fromID(projectID);
		}

		return project;
	}

	public String projectTitle() {
		return project.title();
	}

	public InputStream download() throws CurseException, IOException {
		return NetworkUtils.download(downloadURL());
	}

	public Path download(Path location) throws CurseException, IOException {
		return NIOUtils.download(downloadURL(), location);
	}

	public Path downloadToDirectory(Path directory) throws CurseException, IOException {
		return NIOUtils.downloadToDirectory(downloadURL(), directory);
	}

	public boolean matchesMinimumStability(ReleaseType releaseType) {
		return releaseType.ordinal() >= releaseType().ordinal();
	}

	@Override
	public int hashCode() {
		return id();
	}

	@Override
	public boolean equals(Object anotherObject) {
		if(anotherObject instanceof CurseFile) {
			return ((CurseFile) anotherObject).id() == id();
		}

		return false;
	}

	@Override
	public String toString() {
		return "[id=" + id() + ",name=\"" + name() + "\"]";
	}

	private static TRLList<Integer> getDependencyIDs(List<AddOnFileDependency> dependencies) {
		if(dependencies == null) {
			return new TRLList<>();
		}

		return dependencies.stream().map(dependency -> dependency.AddOnId).
				collect(TRLCollectors.toImmutableList());
	}

	@SuppressWarnings("all")
	private static TRLList<Integer> getDependencies(URL url) throws CurseException {
		//TODO parse from HTML - get dependencies and dependencyIDs
		return null;
	}
}
