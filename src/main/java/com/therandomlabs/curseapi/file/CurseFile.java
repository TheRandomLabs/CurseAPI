package com.therandomlabs.curseapi.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.EnumMap;
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
import com.therandomlabs.curseapi.game.Game;
import com.therandomlabs.curseapi.game.GameVersion;
import com.therandomlabs.curseapi.game.GameVersions;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.InvalidProjectIDException;
import com.therandomlabs.curseapi.project.Member;
import com.therandomlabs.curseapi.RelationType;
import com.therandomlabs.curseapi.util.Documents;
import com.therandomlabs.curseapi.util.URLs;
import com.therandomlabs.curseapi.util.Utils;
import com.therandomlabs.curseapi.widget.FileInfo;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.io.IOUtils;
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
	private final Game game;

	private final int id;
	private final String name;

	private final ReleaseType releaseType;
	private final ZonedDateTime uploadTime;
	private final int downloads;

	private final TRLList<String> gameVersionStrings;
	private final String gameVersionString;

	private TRLList<GameVersion> gameVersions;
	private GameVersion gameVersion;

	private CurseProject project;

	private FileStatus status = FileStatus.NORMAL;

	private URL url;
	private String urlString;

	private String nameOnDisk;

	private String downloadURLString;
	private URL downloadURL;

	private String fileSize;
	private String md5;

	private Member uploader;
	private String uploaderUsername;

	private Map<RelationType, TRLList<Integer>> dependencyIDs;
	private Map<RelationType, TRLList<CurseProject>> dependencies =
			new EnumMap<>(RelationType.class);

	private Element changelogHTML;
	private String changelog;
	private boolean hasChangelog;

	private boolean hasNoProject;

	public CurseFile(CurseProject project, int id, URL url, Element document)
			throws CurseException {
		projectID = project.id();
		game = project.game();

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

		gameVersionStrings = gameVersions.toImmutableList();
		gameVersionString = gameVersions.get(0);

		this.gameVersions = game.versionHandler().get(gameVersionStrings).toImmutableList();
		gameVersion = this.gameVersions.isEmpty() ? GameVersions.UNKNOWN : this.gameVersions.get(0);

		downloads = Integer.parseInt(Documents.getValue(document,
				"class=details-info;class=info-data=4;text").replaceAll(",", ""));
		uploadTime = Utils.parseTime(Documents.getValue(document,
				"class=details-info;attr=data-epoch;attr=data-epoch"));
	}

	public CurseFile(int projectID, Game game, AddOnFile info) throws CurseException {
		this(projectID, game, null, info.FileStatus, info.Id, info.FileName, info.FileNameOnDisk,
				info.releaseType(), info.FileDate, null, -1, getDependencyIDs(info.Dependencies),
				info.GameVersion);
	}

	public CurseFile(CurseProject project, FileInfo info) throws CurseException {
		this(project.id(), project.game(), project, FileStatus.NORMAL, info.id, info.name, null,
				info.type, info.uploaded_at, info.filesize, info.downloads, null, info.versions);
	}

	private CurseFile(int projectID, int id) {
		this.projectID = projectID;
		game = Game.UNKNOWN;
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
		dependencyIDs = new EnumMap<>(RelationType.class);
		dependencies = new EnumMap<>(RelationType.class);
		gameVersionStrings = new ImmutableList<>("Unknown");
		gameVersionString = "Unknown";
		gameVersions = new ImmutableList<>(GameVersions.UNKNOWN);
		gameVersion = GameVersions.UNKNOWN;
		changelogHTML = NO_CHANGELOG_PROVIDED;
		changelog = NO_CHANGELOG_PROVIDED_STRING;
		hasNoProject = true;
	}

	private CurseFile(int projectID, Game game, CurseProject project, FileStatus status, int id,
			String name, String nameOnDisk, ReleaseType releaseType, String uploadTime,
			String fileSize, int downloads, Map<RelationType, TRLList<Integer>> dependencyIDs,
			String[] gameVersions) throws CurseException {
		this.projectID = projectID;
		this.game = game;

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

		gameVersionStrings = gameVersionList.toImmutableList();
		gameVersionString = gameVersionList.get(0);

		this.gameVersions = game.versionHandler().get(gameVersionStrings).toImmutableList();
		gameVersion = this.gameVersions.isEmpty() ? GameVersions.UNKNOWN : this.gameVersions.get(0);
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

			ThreadUtils.splitWorkload(
					CurseAPI.getMaximumThreads(),
					ids.size(),
					index -> dependencyList.add(CurseProject.fromID(ids.get(index)))
			);

			dependencies.put(relationType, dependencyList.toImmutableList());
		}

		return dependencies.get(relationType);
	}

	public TRLList<CurseFile> dependenciesRecursive() throws CurseException {
		return dependenciesRecursive(new FilePredicate().withGameVersions(gameVersions));
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

		ThreadUtils.splitWorkload(
				CurseAPI.getMaximumThreads(),
				firstIterationDependencies.size(),
				index -> retrieveDependencies(
						files,
						predicate,
						dependencies,
						firstIterationDependencies,
						index
				)
		);

		dependencies.remove(null);
		return new TRLList<>(dependencies);
	}

	public String gameVersionString() {
		return gameVersionString;
	}

	public TRLList<String> gameVersionStrings() {
		return gameVersionStrings;
	}

	@SuppressWarnings("unchecked")
	public <G extends GameVersion> G gameVersion() {
		return (G) gameVersion;
	}

	@SuppressWarnings("unchecked")
	public <G extends GameVersion> TRLList<G> gameVersions() {
		return (TRLList<G>) gameVersions;
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
		changelog();
		return hasChangelog;
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

		String trimmedChangelog = changelog.trim().toLowerCase(Locale.ENGLISH);

		if(StringUtils.lastChar(trimmedChangelog) == '.') {
			trimmedChangelog = StringUtils.removeLastChar(trimmedChangelog);
		}

		hasChangelog = !trimmedChangelog.equals("no changelog provided") &&
				!trimmedChangelog.equals("n/a");

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
		return IOUtils.download(downloadURL(), location);
	}

	public Path downloadToDirectory(Path directory) throws CurseException, IOException {
		return IOUtils.downloadToDirectory(downloadURL(), directory);
	}

	public boolean matchesMinimumStability(ReleaseType stability) {
		return releaseType.matchesMinimumStability(stability);
	}

	private void retrieveDependencies(Map<Integer, Integer> files, FilePredicate predicate,
			Set<CurseFile> dependencies, List<CurseFile> firstIterationDependencies, int index)
			throws CurseException {
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
	}

	private CurseFile getFile(Map<Integer, Integer> files, int projectID, FilePredicate predicate)
			throws CurseException {
		if(files.containsKey(projectID)) {
			final int fileID = files.get(projectID);

			if(CurseAPI.isValidFileID(fileID)) {
				return getFile(projectID, game, files.get(projectID));
			}
		}

		if(!CurseAPI.isCurseMetaEnabled()) {
			return CurseProject.fromID(projectID).latestFile(predicate);
		}

		final Set<String> gameVersions = predicate.gameVersionStrings();

		final CurseFileList fileList = getFiles(projectID, game);
		final CurseFile fallback = fileList.latestWithGameVersionString(gameVersions);

		fileList.filterMinimumStability(predicate.minimumStability());
		final CurseFile file = fileList.latestWithGameVersionString(gameVersions);

		//Bypass minimumStability if there are no dependency files matching it
		return file == null ? fallback : file;
	}

	private String getDownloadURLString() throws CurseException {
		try {
			final String idString = Integer.toString(id);

			//First three digits/other digits
			final String id1 = StringUtils.removeLastChars(idString, 3);
			//Remove leading zeros
			final String id2 = idString.substring(id1.length()).replaceAll("^0+", "");
			final String fileName = URLEncoder.encode(nameOnDisk(), "UTF-8").replaceAll("%20",
					"+");

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
				dependencyIDs = new EnumMap<>(RelationType.class);

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
		return getFiles(projectID, Game.UNKNOWN);
	}

	public static CurseFileList getFiles(int projectID, Game game) throws CurseException {
		if(!CurseAPI.isCurseMetaEnabled()) {
			return CurseProject.fromID(projectID).files();
		}

		final TRLList<AddOnFile> files = CurseMeta.getFiles(projectID);
		final CurseFileList curseFiles = new CurseFileList(files.size());

		for(AddOnFile file : files) {
			curseFiles.add(new CurseFile(projectID, game, file));
		}

		curseFiles.sortByNewest();
		return curseFiles;
	}

	public static CurseFileList getFilesBetween(int projectID, int oldID, int newID)
			throws CurseException {
		return getFilesBetween(projectID, Game.UNKNOWN, oldID, newID);
	}

	public static CurseFileList getFilesBetween(int projectID, Game game, int oldID, int newID)
			throws CurseException {
		if(!CurseAPI.isCurseMetaEnabled()) {
			return CurseProject.fromID(projectID).filesBetween(oldID, newID);
		}

		final CurseFileList files = getFiles(projectID, game);
		files.between(oldID, newID);
		return files;
	}

	public static CurseFile getFile(int projectID, int fileID) throws CurseException {
		return getFile(projectID, Game.UNKNOWN, fileID);
	}

	public static CurseFile getFile(int projectID, Game game, int fileID) throws CurseException {
		if(!CurseAPI.isCurseMetaEnabled()) {
			return CurseProject.fromID(projectID).fileWithID(fileID);
		}

		return new CurseFile(projectID, game, CurseMeta.getFile(projectID, fileID));
	}

	public static CurseFile getClosestFile(int projectID, int fileID, boolean preferOlder)
			throws CurseException {
		return getClosestFile(projectID, Game.UNKNOWN, fileID, preferOlder);
	}

	public static CurseFile getClosestFile(int projectID, Game game, int fileID,
			boolean preferOlder) throws CurseException {
		if(!CurseAPI.isCurseMetaEnabled()) {
			return CurseProject.fromID(projectID).fileClosestToID(fileID, preferOlder);
		}

		return getFiles(projectID, game).fileClosestToID(fileID, preferOlder);
	}

	public static CurseFile nullFile(int projectID, int fileID) {
		return new CurseFile(projectID, fileID);
	}

	private static Map<RelationType, TRLList<Integer>> getDependencyIDs(
			List<AddOnFileDependency> dependencies) {
		if(dependencies == null || dependencies.isEmpty()) {
			return Collections.emptyMap();
		}

		final Map<RelationType, TRLList<Integer>> ids = new EnumMap<>(RelationType.class);
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
