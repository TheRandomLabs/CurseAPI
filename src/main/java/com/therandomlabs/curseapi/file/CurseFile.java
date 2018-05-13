package com.therandomlabs.curseapi.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.curseapi.cursemeta.AddOnFile;
import com.therandomlabs.curseapi.cursemeta.AddOnFileDependency;
import com.therandomlabs.curseapi.cursemeta.CurseMeta;
import com.therandomlabs.curseapi.cursemeta.CurseMetaException;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.InvalidProjectIDException;
import com.therandomlabs.curseapi.project.RelationType;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.MiscUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.curseapi.widget.FileInfo;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.io.NIOUtils;
import com.therandomlabs.utils.io.NetUtils;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.misc.ThreadUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

//TODO Additional Files
public final class CurseFile implements Comparable<CurseFile> {
	private final int projectID;
	private CurseProject project;
	private FileStatus status;
	private URL url;
	private String urlString;
	private final int id;
	private final String name;
	private final String nameOnDisk;
	private String downloadURLString;
	private URL downloadURL;
	private final ReleaseType releaseType;
	private final ZonedDateTime uploadTime;
	private final String fileSize;
	private final int downloads;
	private String md5;
	private String uploader;
	private String uploaderURLString;
	private URL uploaderURL;
	private final Map<RelationType, TRLList<Integer>> dependencyIDs;
	private Map<RelationType, TRLList<CurseProject>> dependencies;
	private final TRLList<String> gameVersions;
	private final TRLList<MinecraftVersion> minecraftVersions;
	private Element changelogHTML;
	private String changelog;
	private boolean noCurseForgeURL;
	private boolean hasNoProject;

	public CurseFile(int projectID, AddOnFile info) throws CurseException {
		this(projectID, null, info.FileStatus, info.Id, info.FileName, info.FileNameOnDisk,
				info.DownloadURL, info.releaseType(), info.FileDate, null, -1,
				getDependencyIDs(info.Dependencies), info.GameVersion);
	}

	public CurseFile(CurseProject project, AddOnFile info) throws CurseException {
		this(project.id(), project, info.FileStatus, info.Id, info.FileName, info.FileNameOnDisk,
				info.DownloadURL, info.releaseType(), info.FileDate, null, -1,
				getDependencyIDs(info.Dependencies), info.GameVersion);
	}

	public CurseFile(int projectID, FileInfo info) throws CurseException {
		this(projectID, null, FileStatus.NORMAL, info.id, info.name, null, null, info.type,
				info.uploaded_at, info.filesize, info.downloads, null, info.versions);
	}

	public CurseFile(CurseProject project, FileInfo info) throws CurseException {
		this(project.id(), project, FileStatus.NORMAL, info.id, info.name, null, null, info.type,
				info.uploaded_at, info.filesize, info.downloads, null, info.versions);
	}

	public CurseFile(int projectID, CurseProject project, FileStatus status, int id, String name,
			String nameOnDisk, URL downloadURL, ReleaseType releaseType, String uploadTime,
			String fileSize, int downloads, Map<RelationType, TRLList<Integer>> dependencyIDs,
			String[] gameVersions) throws CurseException {
		this.projectID = projectID;
		this.project = project;
		this.status = status;

		if(project != null) {
			urlString = project.urlString() + "/files/" + id;
			url = URLUtils.url(urlString);
		}

		this.id = id;
		this.name = name;
		this.nameOnDisk = nameOnDisk == null ?
				DocumentUtils.getValue(url, "class=details-info;class=info-data;text") : nameOnDisk;
		this.downloadURL = downloadURL;
		downloadURLString = downloadURL ==  null ? null : this.downloadURL.toString();
		this.releaseType = releaseType;
		this.uploadTime = MiscUtils.parseTime(uploadTime);
		this.fileSize = fileSize;
		this.downloads = downloads;
		this.dependencyIDs =
				dependencyIDs == null ? getDependencies(url) : dependencyIDs;
		this.dependencies = new HashMap<>(dependencyIDs == null ? 1 : dependencyIDs.size());

		final TRLList<String> gameVersionList = new TRLList<>();
		for(String gameVersion : gameVersions) {
			if(!gameVersion.startsWith("Java ")) {
				gameVersionList.add(gameVersion);
			}
		}
		this.gameVersions = gameVersionList.toImmutableList();

		final TRLList<MinecraftVersion> minecraftVersions =
					CollectionUtils.map(this.gameVersions, MinecraftVersion::fromString);
		minecraftVersions.removeIf(Objects::isNull);
		minecraftVersions.sort();
		this.minecraftVersions = minecraftVersions.toImmutableList();
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
		if(url == null && !noCurseForgeURL) {
			try {
				urlString = CurseForge.fromID(projectID) + "/files/" + id;
				url = URLUtils.url(urlString);
				DocumentUtils.get(url);
			} catch(CurseException ex) {
				if(!(ex.getCause() instanceof FileNotFoundException) &&
						!(ex instanceof InvalidProjectIDException)) {
					throw ex;
				}

				urlString = null;
				url = null;
				noCurseForgeURL = true;
				status = FileStatus.SEMI_NORMAL;
			}
		}

		return url;
	}

	public String urlString() throws CurseException {
		url();
		return urlString;
	}

	public URL downloadURL() throws CurseException {
		if(downloadURL == null) {
			downloadURL = CurseForge.getFileURL(projectID, id);
			downloadURLString = downloadURL.toString();
		}

		return downloadURL;
	}

	public String downloadURLString() throws CurseException {
		downloadURL();
		return downloadURLString;
	}

	public ReleaseType releaseType() {
		return releaseType;
	}

	public TRLList<Integer> dependencyIDs() {
		return dependencyIDs(RelationType.ALL_TYPES);
	}

	public TRLList<Integer> dependencyIDs(RelationType relationType) {
		final TRLList<Integer> ids = dependencyIDs.get(relationType);
		return ids == null ? ImmutableList.empty() : ids;
	}

	public TRLList<CurseProject> dependencies() throws CurseException {
		return dependencies(RelationType.ALL_TYPES);
	}

	public TRLList<CurseProject> dependencies(RelationType relationType) throws CurseException {
		if(dependencies.get(relationType) == null) {
			final TRLList<Integer> ids = dependencyIDs(relationType);
			if(ids.isEmpty()) {
				return ImmutableList.empty();
			}

			final TRLList<CurseProject> dependencyList = new TRLList<>(ids.size());
			ThreadUtils.splitWorkload(CurseAPI.getMaximumThreads(), ids.size(),
					index -> dependencyList.add(CurseProject.fromID(ids.get(index))));
			dependencies.put(relationType, dependencyList.toImmutableList());
		}

		return dependencies.get(relationType);
	}

	public TRLList<CurseFile> dependenciesRecursiveMC(ReleaseType minimumStability,
			MinecraftVersion... mcVersions) throws CurseException {
		return dependenciesRecursiveMC(minimumStability, new ImmutableList<>(mcVersions));
	}

	public TRLList<CurseFile> dependenciesRecursiveMC(ReleaseType minimumStability,
			Collection<MinecraftVersion> mcVersions) throws CurseException {
		return dependenciesRecursive(minimumStability,
				CollectionUtils.toStrings(MinecraftVersion.getVersions(mcVersions)));
	}

	public TRLList<CurseFile> dependenciesRecursive(ReleaseType minimumStability,
			String... gameVersions) throws CurseException {
		return dependenciesRecursive(minimumStability, new ImmutableList<>(gameVersions));
	}

	public TRLList<CurseFile> dependenciesRecursive(ReleaseType minimumStability,
			Collection<String> gameVersions) throws CurseException {
		return dependenciesRecursive(Collections.emptyMap(), minimumStability, gameVersions);
	}

	public TRLList<CurseFile> dependenciesRecursiveMC(Collection<CurseFile> files,
			ReleaseType minimumStability, MinecraftVersion... mcVersions)
			throws CurseException {
		return dependenciesRecursiveMC(files, minimumStability, new ImmutableList<>(mcVersions));
	}

	public TRLList<CurseFile> dependenciesRecursiveMC(Collection<CurseFile> files,
			ReleaseType minimumStability, Collection<MinecraftVersion> mcVersions)
			throws CurseException {
		return dependenciesRecursive(files, minimumStability,
				CollectionUtils.toStrings(MinecraftVersion.getVersions(mcVersions)));
	}

	public TRLList<CurseFile> dependenciesRecursive(Collection<CurseFile> files,
			ReleaseType minimumStability, String... gameVersions) throws CurseException {
		return dependenciesRecursive(files, minimumStability, new ImmutableList<>(gameVersions));
	}

	public TRLList<CurseFile> dependenciesRecursive(Collection<CurseFile> files,
			ReleaseType minimumStability, Collection<String> gameVersions) throws CurseException {
		final Map<Integer, Integer> map = new HashMap<>(files.size());
		for(CurseFile file : files) {
			map.put(file.projectID, file.id);
		}
		return dependenciesRecursive(map, minimumStability, gameVersions);
	}

	public TRLList<CurseFile> dependenciesRecursiveMC(Map<Integer, Integer> files,
			ReleaseType minimumStability, MinecraftVersion... mcVersions)
			throws CurseException {
		return dependenciesRecursiveMC(files, minimumStability, new ImmutableList<>(mcVersions));
	}

	public TRLList<CurseFile> dependenciesRecursiveMC(Map<Integer, Integer> files,
			ReleaseType minimumStability, Collection<MinecraftVersion> mcVersions)
			throws CurseException {
		return dependenciesRecursive(files, minimumStability,
				CollectionUtils.toStrings(MinecraftVersion.getVersions(mcVersions)));
	}

	public TRLList<CurseFile> dependenciesRecursive(Map<Integer, Integer> files,
			ReleaseType minimumStabililty, String... gameVersions) throws CurseException {
		return dependenciesRecursive(files, minimumStabililty, new ImmutableList<>(gameVersions));
	}

	public TRLList<CurseFile> dependenciesRecursive(Map<Integer, Integer> files,
			ReleaseType minimumStabililty, Collection<String> gameVersions) throws CurseException {
		final TRLList<CurseFile> dependencies = new TRLList<>();
		final Queue<CurseFile> toCheck = new PriorityQueue<>();
		toCheck.add(this);

		while(!toCheck.isEmpty()) {
			final CurseFile file = toCheck.poll();

			for(int id : file.dependencyIDs(RelationType.REQUIRED_LIBRARY)) {
				toCheck.add(getFile(files, id, minimumStabililty, gameVersions));
			}

			if(file != this) {
				dependencies.add(file);
			}
		}

		return dependencies;
	}

	private CurseFile getFile(Map<Integer, Integer> files, int projectID,
			ReleaseType minimumStability, Collection<String> gameVersions) throws CurseException {
		if(files.containsKey(projectID)) {
			final int fileID = files.get(projectID);
			if(fileID >= CurseAPI.MIN_PROJECT_ID) {
				return fromID(projectID, files.get(projectID));
			}
		}

		final CurseFileList fileList = filesFromProjectID(projectID);
		final CurseFile fallback = fileList.latest(gameVersions);

		fileList.filterMinimumStability(minimumStability);
		final CurseFile file = fileList.latest(gameVersions);

		//Bypass minimumStability if there are no dependency files matching it
		return file == null ? fallback : file;
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

	public String md5() throws CurseException {
		ensureHTMLDataRetrieved();
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
			if(url == null || !DocumentUtils.isCached(url)) {
				try {
					changelogHTML = CurseMeta.getChangelog(projectID, id);
				} catch(CurseMetaException ex) {
					changelogHTML = Jsoup.parse("No changelog provided");
				}
			} else {
				changelogHTML = DocumentUtils.get(url, "class=logbox");
			}

			changelog = DocumentUtils.getPlainText(changelogHTML);
		}

		return changelogHTML;
	}

	public String uploader() throws CurseException {
		ensureHTMLDataRetrieved();
		return uploader;
	}

	public String uploaderURLString() throws CurseException {
		ensureHTMLDataRetrieved();
		return uploaderURLString;
	}

	public URL uploaderURL() throws CurseException {
		ensureHTMLDataRetrieved();
		return uploaderURL;
	}

	public int projectID() {
		return projectID;
	}

	public CurseProject project() throws CurseException {
		if(project == null && !hasNoProject) {
			try {
				project = CurseProject.fromID(projectID);
			} catch(InvalidProjectIDException ex) {
				hasNoProject = true;
			}
		}

		return project;
	}

	public String projectTitle() {
		return project.title();
	}

	public InputStream download() throws CurseException, IOException {
		return NetUtils.download(downloadURL());
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

	@Override
	public int compareTo(CurseFile file) {
		return Integer.compare(id, file.id);
	}

	public static CurseFileList filesFromProjectID(int projectID) throws CurseException {
		if(CurseAPI.isAvoidingCurseMeta()) {
			return CurseProject.fromID(projectID).files();
		}

		final TRLList<AddOnFile> files = CurseMeta.getFiles(projectID);
		final CurseFileList curseFiles = new CurseFileList(files.size());

		for(AddOnFile file : files) {
			curseFiles.add(new CurseFile(projectID, file));
		}

		curseFiles.sortByNewest();
		return curseFiles;
	}

	public static CurseFile fromID(int projectID, int fileID) throws CurseException {
		return new CurseFile(projectID, CurseMeta.getFile(projectID, fileID));
	}

	private void ensureHTMLDataRetrieved() throws CurseException {
		if(md5 != null || noCurseForgeURL || url() == null) {
			return;
		}

		md5 = DocumentUtils.getValue(url, "class=md5;text");
		uploader = DocumentUtils.getValue(url, "class=user-tag;tag=a=1;text");
		uploaderURLString = DocumentUtils.getValue(url, "class=user-tag;tag=a=1;absUrl=href");
		uploaderURL = URLUtils.url(uploaderURLString);
	}

	private static Map<RelationType, TRLList<Integer>> getDependencyIDs(
			List<AddOnFileDependency> dependencies) {
		if(dependencies == null || dependencies.isEmpty()) {
			return Collections.emptyMap();
		}

		final Map<RelationType, TRLList<Integer>> ids = new HashMap<>(RelationType.values().length);
		final TRLList<Integer> all = new TRLList<>(dependencies.size());
		ids.put(RelationType.ALL_TYPES, all);

		for(AddOnFileDependency dependency : dependencies) {
			ids.computeIfAbsent(dependency.Type, type -> new TRLList<>());
			ids.get(dependency.Type).add(dependency.AddOnId);
			all.add(dependency.AddOnId);
		}

		for(Map.Entry<RelationType, TRLList<Integer>> entry : ids.entrySet()) {
			ids.put(entry.getKey(), entry.getValue().toImmutableList());
		}

		return ids;
	}

	@SuppressWarnings("all")
	private static Map<RelationType, TRLList<Integer>> getDependencies(URL url)
			throws CurseException {
		//TODO parse from HTML - get dependencies and dependencyIDs
		return Collections.emptyMap();
	}
}
