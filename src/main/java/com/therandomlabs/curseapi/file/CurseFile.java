package com.therandomlabs.curseapi.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseForge;
import com.therandomlabs.curseapi.cursemeta.AddOnFile;
import com.therandomlabs.curseapi.cursemeta.AddOnFileDependency;
import com.therandomlabs.curseapi.cursemeta.CurseMeta;
import com.therandomlabs.curseapi.cursemeta.CurseMetaException;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.InvalidProjectIDException;
import com.therandomlabs.curseapi.project.Member;
import com.therandomlabs.curseapi.project.RelationType;
import com.therandomlabs.curseapi.util.Documents;
import com.therandomlabs.curseapi.util.URLs;
import com.therandomlabs.curseapi.util.Utils;
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
import org.jsoup.select.Elements;

//TODO Additional Files
public final class CurseFile implements Comparable<CurseFile> {
	private static final String NO_CHANGELOG_PROVIDED_STRING = "No changelog provided";
	private static final Element NO_CHANGELOG_PROVIDED = Jsoup.parse(NO_CHANGELOG_PROVIDED_STRING);

	private static final String ID_1 = "::ID_1::";
	private static final String ID_2 = "::ID_2::";
	private static final String FILE_NAME = "::FILE_NAME::";
	private static final String FILE_DOWNLOAD_URL =
			"https://media.forgecdn.net/files/" + ID_1 + "/" + ID_2 + "/" + FILE_NAME;

	private final int projectID;
	private CurseProject project;
	private FileStatus status = FileStatus.NORMAL;
	private URL url;
	private String urlString;
	private final int id;
	private final String name;
	private String nameOnDisk;
	private String downloadURLString;
	private URL downloadURL;
	private final ReleaseType releaseType;
	private final ZonedDateTime uploadTime;
	private String fileSize;
	private final int downloads;
	private String md5;
	private Member uploader;
	private String uploaderUsername;
	private Map<RelationType, TRLList<Integer>> dependencyIDs;
	private Map<RelationType, TRLList<CurseProject>> dependencies = new HashMap<>();
	private final TRLList<String> gameVersions;
	private final TRLList<MinecraftVersion> minecraftVersions;
	private Element changelogHTML;
	private String changelog;
	private boolean hasNoProject;

	private CurseFile(int projectID, int id) {
		this.projectID = projectID;
		this.id = id;
		project = CurseProject.nullProject(projectID);
		status = FileStatus.DELETED;
		name = "Null File";
		nameOnDisk = "null-file";
		downloadURLString = null;
		downloadURL = null;
		releaseType = ReleaseType.ALPHA;
		uploadTime = ZonedDateTime.now();
		fileSize = "0.00 KB";
		downloads = 0;
		md5 = "ffffffffffffffffffffffffffffffff";
		uploader = Member.UNKNOWN;
		uploaderUsername = "Unknown";
		dependencyIDs = new HashMap<>();
		this.dependencies = new HashMap<>();
		this.gameVersions = new ImmutableList<>("Unknown");
		this.minecraftVersions = new ImmutableList<>();
		changelogHTML = NO_CHANGELOG_PROVIDED;
		changelog = NO_CHANGELOG_PROVIDED_STRING;
		hasNoProject = true;
	}

	public CurseFile(CurseProject project, int id, URL url, Element document)
			throws CurseException {
		projectID = project.id();
		this.project = project;
		this.id = id;
		this.url = url;
		urlString = url.toString();

		ensureHTMLDataRetrieved(document);

		name = Documents.getValue(document, "class=details-header;class=overflow-tip;text");
		downloadURLString = getDownloadURLString();
		downloadURL = URLs.of(downloadURLString);
		releaseType = ReleaseType.fromName(Documents.getValue(document,
				"class=project-file-release-type;class=tip;attr=title"));

		final Elements versions = document.getElementsByClass("details-versions").
				get(0).getElementsByTag("li");

		final TRLList<String> gameVersions = CollectionUtils.map(versions, Element::text);
		gameVersions.removeIf(version -> version.startsWith("Java "));

		this.gameVersions = gameVersions.toImmutableList();
		minecraftVersions = MinecraftVersion.fromStrings(gameVersions);
		downloads = Integer.parseInt(Documents.getValue(document,
				"class=details-info;class=info-data=4;text").replaceAll(",", ""));
		uploadTime = Utils.parseTime(Documents.getValue(document,
				"class=details-info;attr=data-epoch;attr=data-epoch"));
	}

	public CurseFile(int projectID, AddOnFile info) throws CurseException {
		this(projectID, null, info.FileStatus, info.Id, info.FileName, info.FileNameOnDisk,
				info.releaseType(), info.FileDate, null, -1, getDependencyIDs(info.Dependencies),
				info.GameVersion);
	}

	public CurseFile(CurseProject project, FileInfo info) throws CurseException {
		this(project.id(), project, FileStatus.NORMAL, info.id, info.name, null, info.type,
				info.uploaded_at, info.filesize, info.downloads, null, info.versions);
	}

	private CurseFile(int projectID, CurseProject project, FileStatus status, int id, String name,
			String nameOnDisk, ReleaseType releaseType, String uploadTime,
			String fileSize, int downloads, Map<RelationType, TRLList<Integer>> dependencyIDs,
			String[] gameVersions) throws CurseException {
		this.projectID = projectID;
		this.project = project;
		this.status = status;

		if(project != null) {
			urlString = project.urlString() + "/files/" + id;
			url = URLs.of(urlString);
		}

		this.id = id;
		this.name = name;
		this.nameOnDisk = nameOnDisk;
		this.releaseType = releaseType;
		this.uploadTime = Utils.parseTime(uploadTime);
		this.fileSize = fileSize;
		this.downloads = downloads;
		this.dependencyIDs = dependencyIDs;

		final TRLList<String> gameVersionList = new TRLList<>();

		for(String gameVersion : gameVersions) {
			//Stay consistent with CurseMeta
			//CurseMeta doesn't include Java versions for some reason
			if(!gameVersion.startsWith("Java ")) {
				gameVersionList.add(gameVersion);
			}
		}

		this.gameVersions = gameVersionList.toImmutableList();
		this.minecraftVersions = MinecraftVersion.fromStrings(gameVersionList).toImmutableList();
	}

	public boolean isNull() {
		return "ffffffffffffffffffffffffffffffff".equals(md5);
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

	public String nameOnDisk() throws CurseException {
		ensureHTMLDataRetrieved();
		return nameOnDisk;
	}

	public URL url() throws CurseException {
		if(url == null && !hasNoProject &&
				(status == FileStatus.NORMAL || status == FileStatus.SEMI_NORMAL)) {
			try {
				urlString = CurseForge.fromIDNoValidation(projectID) + "/files" + id;
				url = URLs.of(urlString);
			} catch(InvalidProjectIDException ex) {
				hasNoProject = true;
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
			ensureHTMLDataRetrieved();
			downloadURLString = getDownloadURLString();
			downloadURL = URLs.of(downloadURLString);
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

	public TRLList<Integer> dependencyIDs() throws CurseException {
		return dependencyIDs(RelationType.ALL_TYPES);
	}

	public TRLList<Integer> dependencyIDs(RelationType relationType) throws CurseException {
		if(dependencyIDs == null) {
			ensureHTMLDataRetrieved();

			if(dependencyIDs == null) {
				return new TRLList<>();
			}
		}

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

	public TRLList<CurseFile> dependenciesRecursive(FilePredicate predicate) throws CurseException {
		return dependenciesRecursive(new ConcurrentHashMap<>(), predicate);
	}

	public TRLList<CurseFile> dependenciesRecursive(Map<Integer, Integer> files,
			FilePredicate predicate) throws CurseException {
		final Set<CurseFile> dependencies = new HashSet<>();
		final List<CurseFile> firstIterationDependencies = new TRLList<>();

		for(int id : dependencyIDs(RelationType.REQUIRED_LIBRARY)) {
			final CurseFile file = getFile(files, id, predicate);
			firstIterationDependencies.add(file);
			dependencies.add(file);
		}

		ThreadUtils.splitWorkload(CurseAPI.getMaximumThreads(), firstIterationDependencies.size(),
				index -> {
			final Queue<CurseFile> toCheck = new PriorityBlockingQueue<>();
			final CurseFile toAdd = firstIterationDependencies.get(index);

			if(toAdd != null) {
				toCheck.add(toAdd);
			}

			while(!toCheck.isEmpty()) {
				final CurseFile file = toCheck.poll();

				if(file == null) {
					continue;
				}

				for(int id : file.dependencyIDs(RelationType.REQUIRED_LIBRARY)) {
					toCheck.add(getFile(files, id, predicate));
				}

				if(file != this) {
					dependencies.add(file);
				}
			}
		});

		dependencies.remove(null);
		return new TRLList<>(dependencies);
	}

	private CurseFile getFile(Map<Integer, Integer> files, int projectID, FilePredicate predicate)
			throws CurseException {
		if(files.containsKey(projectID)) {
			final int fileID = files.get(projectID);
			if(fileID >= CurseAPI.MIN_FILE_ID) {
				return getFile(projectID, files.get(projectID));
			}
		}

		if(!CurseAPI.isCurseMetaEnabled()) {
			return CurseProject.fromID(projectID).latestFile(predicate);
		}

		final Set<String> gameVersions = predicate.gameVersions();

		final CurseFileList fileList = getFiles(projectID);
		final CurseFile fallback = fileList.latest(gameVersions);

		fileList.filterMinimumStability(predicate.minimumStability());
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
		return minecraftVersions.isEmpty() ? MinecraftVersion.UNKNOWN : minecraftVersions.get(0);
	}

	public TRLList<MinecraftVersion> minecraftVersions() {
		return minecraftVersions;
	}

	public ZonedDateTime uploadTime() {
		return uploadTime;
	}

	public String fileSize() throws CurseException {
		ensureHTMLDataRetrieved();
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
		return changelog(false);
	}

	public String changelog(boolean minimalOverhead) throws CurseException {
		changelogHTML(minimalOverhead);
		return changelog;
	}

	public Element changelogHTML() throws CurseException {
		return changelogHTML(false);
	}

	//If minimalOverhead is true, use CurseMeta if it is enabled and the changelog is not
	//already cached since CurseMeta is faster than downloading a full document
	//Changelog generators are the main use case
	public Element changelogHTML(boolean minimalOverhead) throws CurseException {
		if(changelogHTML == null) {
			if(minimalOverhead && CurseAPI.isCurseMetaEnabled()) {
				try {
					changelogHTML = CurseMeta.getChangelog(projectID, id);
					getChangelogString();
				} catch(CurseMetaException ex) {
					changelogHTML = NO_CHANGELOG_PROVIDED;
					changelog = NO_CHANGELOG_PROVIDED_STRING;
				}
			} else {
				ensureHTMLDataRetrieved();

				if(changelogHTML == null) {
					changelogHTML = NO_CHANGELOG_PROVIDED;
					changelog = NO_CHANGELOG_PROVIDED_STRING;
				}
			}
		}

		return changelogHTML;
	}

	public Member uploader() throws CurseException {
		final CurseProject project = project();

		if(project == null) {
			return null;
		}

		if(uploader == null) {
			ensureHTMLDataRetrieved();

			if(uploaderUsername == null) {
				return null;
			}

			for(Member member : project.members()) {
				if(member.username().equals(uploaderUsername)) {
					uploader = member;
				}
			}
		}

		return uploader;
	}

	public String uploaderUsername() throws CurseException {
		ensureHTMLDataRetrieved();
		return uploaderUsername;
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

	public boolean matchesMinimumStability(ReleaseType stability) {
		return releaseType.matchesMinimumStability(stability);
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object anotherObject) {
		if(anotherObject instanceof CurseFile) {
			return ((CurseFile) anotherObject).id == id;
		}

		return false;
	}

	@Override
	public String toString() {
		return "[id=" + id + ",name=\"" + name + "\"]";
	}

	@Override
	public int compareTo(CurseFile file) {
		return Integer.compare(id, file.id);
	}

	private String getDownloadURLString() throws CurseException {
		try {
			final String idString = Integer.toString(id);

			//First three digits/other digits
			final String id1 = StringUtils.removeLastChars(idString, 3);
			//Remove leading zeros
			final String id2 = idString.substring(id1.length()).replaceAll("^0+", "");
			final String fileName = URLEncoder.encode(nameOnDisk(), "UTF-8").replaceAll("%20", "+");

			return FILE_DOWNLOAD_URL.replace(ID_1, id1).replace(ID_2, id2).
					replace(FILE_NAME, fileName);
		} catch(UnsupportedEncodingException ignored) {}

		return null;
	}

	private void ensureHTMLDataRetrieved() throws CurseException {
		ensureHTMLDataRetrieved(null);
	}

	private void ensureHTMLDataRetrieved(Element document) throws CurseException {
		if(htmlDataRetrieved() || url() == null) {
			return;
		}

		if(document == null) {
			document = Documents.get(url);
		}

		nameOnDisk = Documents.get(document, "class=details-info;class=info-data").textNodes().
				get(0).getWholeText();
		fileSize = Documents.getValue(document, "class=details-info;class=info-data=3;text");
		changelogHTML = document.getElementsByClass("logbox").get(0);
		getChangelogString();
		md5 = Documents.getValue(document, "class=md5;text");
		uploaderUsername = Documents.getValue(document, "class=user-tag;tag=a=1;text");

		if(dependencyIDs == null) {
			try {
				final Elements relatedProjects =
						document.getElementsByClass("details-related-projects");

				if(relatedProjects.isEmpty()) {
					return;
				}

				final Elements elements = relatedProjects.get(0).getAllElements();

				RelationType type = null;
				dependencyIDs = new HashMap<>();

				for(Element element : elements) {
					if("h5".equals(element.tagName())) {
						type = RelationType.fromName(element.text());
						continue;
					}

					if(!"ul".equals(element.tagName())) {
						continue;
					}

					final Elements related = element.getElementsByAttribute("href");
					final TRLList<Integer> ids = new TRLList<>();

					for(Element element2 : related) {
						ids.add(Integer.parseInt(element2.attr("href").split("/")[2]));
					}

					dependencyIDs.put(type, ids);
				}
			} catch(IndexOutOfBoundsException | NullPointerException | NumberFormatException ex) {
				dependencyIDs = null;
				throw CurseException.fromThrowable("Error while retrieving dependency IDs for " +
						"file with ID: " + id, ex);
			}
		}
	}

	private boolean htmlDataRetrieved() {
		return nameOnDisk != null;
	}

	private void getChangelogString() {
		changelog = Documents.getPlainText(changelogHTML);

		if(StringUtils.lastChar(changelog) == '\n') {
			changelog = StringUtils.removeLastChar(changelog);
		}
	}

	public static CurseFileList getFiles(int projectID) throws CurseException {
		if(!CurseAPI.isCurseMetaEnabled()) {
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

	public static CurseFileList getFilesBetween(int projectID, int oldID, int newID)
			throws CurseException {
		if(!CurseAPI.isCurseMetaEnabled()) {
			return CurseProject.fromID(projectID).filesBetween(oldID, newID);
		}

		final CurseFileList files = getFiles(projectID);
		files.between(oldID, newID);
		return files;
	}

	public static CurseFile getFile(int projectID, int fileID) throws CurseException {
		if(!CurseAPI.isCurseMetaEnabled()) {
			return CurseProject.fromID(projectID).fileWithID(fileID);
		}

		return new CurseFile(projectID, CurseMeta.getFile(projectID, fileID));
	}

	public static CurseFile getClosestFile(int projectID, int fileID, boolean preferOlder)
			throws CurseException {
		if(!CurseAPI.isCurseMetaEnabled()) {
			return CurseProject.fromID(projectID).fileClosestToID(fileID, preferOlder);
		}

		return getFiles(projectID).fileClosestToID(fileID, preferOlder);
	}

	public static CurseFile nullFile(int projectID, int fileID) {
		return new CurseFile(projectID, fileID);
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
}
