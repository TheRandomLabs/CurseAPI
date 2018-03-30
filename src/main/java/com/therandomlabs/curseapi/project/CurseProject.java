package com.therandomlabs.curseapi.project;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.Game;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.curseapi.curseforge.CurseForgeSite;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFileList;
import com.therandomlabs.curseapi.file.ReleaseType;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.MiscUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.curseapi.widget.DownloadInfo;
import com.therandomlabs.curseapi.widget.DownloadsInfo;
import com.therandomlabs.curseapi.widget.FileInfo;
import com.therandomlabs.curseapi.widget.MemberInfo;
import com.therandomlabs.curseapi.widget.ProjectInfo;
import com.therandomlabs.curseapi.widget.URLInfo;
import com.therandomlabs.curseapi.widget.WidgetAPI;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLCollectors;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.misc.StopSwitch;
import com.therandomlabs.utils.network.NetworkUtils;
import com.therandomlabs.utils.runnable.RunnableWithInput;
import com.therandomlabs.utils.throwable.ThrowableHandling;
import com.therandomlabs.utils.wrapper.Wrapper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//TODO Images, Issues, Source, Pages, Wiki, Get number of relations,
//get relations on a specific page
public final class CurseProject {
	public static final URL PLACEHOLDER_THUMBNAIL;

	public static final int RELATIONS_PER_PAGE = 20;

	private static final List<CurseProject> projects = new CopyOnWriteArrayList<>();

	static {
		URL placeholder = null;

		try {
			placeholder = new URL(
					"https://media-elerium.cursecdn.com/avatars/0/93/635227964539626926.png");
		} catch(MalformedURLException ex) {
			ThrowableHandling.handleUnexpected(ex);
		}

		PLACEHOLDER_THUMBNAIL = placeholder;
	}

	private final Map<RelationType, TRLList<Relation>> dependencies = new HashMap<>();
	private final Map<RelationType, TRLList<Relation>> dependents = new HashMap<>();
	private URL url;
	private URL mainCurseForgeURL;
	private CurseForgeSite site;
	private ProjectInfo widgetInfo;
	private CurseFileList files;
	private TRLList<Category> categories;

	private CurseProject(int id) throws CurseException {
		this(CurseForge.fromID(id));
	}

	private CurseProject(URL url) throws CurseException {
		CurseException.validateProject(url);

		this.url = url;
		this.mainCurseForgeURL = CurseForge.toMainCurseForgeProject(url);
		reload(false);

		projects.add(this);
	}

	public static CurseProject fromID(String id) throws CurseException {
		return fromID(Integer.parseInt(id));
	}

	public static CurseProject fromID(int id) throws CurseException {
		for(CurseProject project : projects) {
			if(project.id() == id) {
				return project;
			}
		}

		return new CurseProject(id);
	}

	public int id() {
		return widgetInfo.id;
	}

	public static CurseProject fromURL(URL url) throws CurseException {
		return fromURL(url, false);
	}

	public static CurseProject fromURL(URL url, boolean followRedirections) throws CurseException {
		if(followRedirections) {
			url = URLUtils.redirect(url);
		}

		for(CurseProject project : projects) {
			if(url.equals(project.url)) {
				return project;
			}
		}

		return new CurseProject(url);
	}

	public static CurseProject fromSlug(String site, String slug) throws CurseException {
		return fromSlug(CurseForgeSite.fromString(site), slug);
	}

	public static CurseProject fromSlug(CurseForgeSite site, String slug) throws CurseException {
		return fromURL(site.url() + "projects/" + slug, true);
	}

	public static CurseProject fromURL(String url) throws CurseException {
		return fromURL(url, false);
	}

	public static CurseProject fromURL(String url, boolean followRedirections)
			throws CurseException {
		return fromURL(URLUtils.url(url), followRedirections);
	}

	public static void clearProjectCache() {
		projects.clear();
	}

	public URL url() {
		return url;
	}

	public URL mainCurseForgeURL() {
		return mainCurseForgeURL;
	}

	public String mainCurseForgeURLString() {
		return mainCurseForgeURL.toString();
	}

	public boolean hasMainCurseForgePage() {
		return mainCurseForgeURL != null;
	}

	public String slug() {
		return ArrayUtils.last(url.getPath().split("/"));
	}

	public ProjectType type() {
		return ProjectType.get(site(), widgetInfo.type);
	}

	public CurseForgeSite site() {
		return site;
	}

	public BufferedImage avatar() throws CurseException, IOException {
		return ImageIO.read(NetworkUtils.download(avatarURL()));
	}

	public URL avatarURL() throws CurseException {
		return URLUtils.url(avatarURLString());
	}

	public String avatarURLString() throws CurseException {
		return DocumentUtils.getValue(url, "class=e-avatar64;absUrl=href");
	}

	public String thumbnailURLString() {
		return widgetInfo.thumbnail.toString();
	}

	public BufferedImage thumbnail() throws IOException {
		return ImageIO.read(NetworkUtils.download(thumbnailURL()));
	}

	public URL thumbnailURL() {
		return widgetInfo.thumbnail;
	}

	public Member owner() {
		return members(MemberType.OWNER).get(0);
	}

	public TRLList<Member> members(MemberType type) {
		final TRLList<Member> members = members();
		members.removeIf(member -> member.type() != type);
		return members;
	}

	public TRLList<Member> members() {
		return Stream.of(widgetInfo.members).map(Member::fromMemberInfo).
				collect(TRLCollectors.toArrayList());
	}

	public int monthlyDownloads() {
		return widgetInfo.downloads.monthly;
	}

	public int downloads() {
		return widgetInfo.downloads.total;
	}

	public ZonedDateTime creationTime() {
		return MiscUtils.parseTime(widgetInfo.created_at);
	}

	public ZonedDateTime lastUpdateTime() throws CurseException {
		if(widgetInfo.files == null) {
			reloadFiles();
		}
		return MiscUtils.parseTime(widgetInfo.files[0].uploaded_at);
	}

	public ReleaseType releaseType() throws CurseException {
		if(widgetInfo.files == null) {
			reloadFiles();
		}
		return widgetInfo.files[0].type;
	}

	public URL donateURL() {
		return widgetInfo.donate;
	}

	public String donateURLString() {
		return widgetInfo.donate.toString();
	}

	public String licenseName() {
		return widgetInfo.license;
	}

	public String licenseText() throws CurseException {
		return DocumentUtils.getPlainText(licenseHTML());
	}

	public Element licenseHTML() throws CurseException {
		return DocumentUtils.get(urlString() + "/license");
	}

	public String urlString() {
		return url.toString();
	}

	public String shortDescription() {
		return widgetInfo.description;
	}

	public String description() throws CurseException {
		return DocumentUtils.getPlainText(descriptionHTML());
	}

	public Element descriptionHTML() throws CurseException {
		return DocumentUtils.get(url, "class=project-description");
	}

	public TRLList<Category> categories() {
		return categories;
	}

	public CurseFile latestFile() throws CurseException {
		if(files != null) {
			return files.latest();
		}

		final List<CurseFile> files = new TRLList<>();
		getFiles(DocumentUtils.get(url + "/files?page=1"), files);
		return files.get(0);
	}

	public CurseFile latestFile(Collection<String> versions) throws CurseException {
		if(files != null) {
			return files.latest(versions);
		}

		final Wrapper<CurseFile> latestFile = new Wrapper<>();
		final StopSwitch stopSwitch = new StopSwitch();

		DocumentUtils.iteratePages(url + "/files?", this::getFiles, file -> {
			if(!latestFile.isLocked() && file.gameVersions().containsAny(versions)) {
				latestFile.set(file);
				latestFile.lock();
				stopSwitch.stop();
			}
		}, stopSwitch, false);

		return latestFile.get();
	}

	public CurseFile latestFile(String... versions) throws CurseException {
		return latestFile(new ImmutableList<>(versions));
	}

	public CurseFile latestFile(MinecraftVersion... versions) throws CurseException {
		return latestFile(CollectionUtils.stringify(MinecraftVersion.getVersions(versions)));
	}

	public CurseFile latestFileWithMCVersionGroup(String version) throws CurseException {
		return latestFile(MinecraftVersion.groupFromString(version));
	}

	public CurseFileList files() throws CurseException {
		return filesDirect().clone();
	}

	private CurseFileList filesDirect() throws CurseException {
		if(files == null) {
			reloadFiles();
		}

		return files;
	}

	public CurseFile fileWithID(int id) throws CurseException {
		return filesDirect().fileWithID(id);
	}

	public CurseFile fileClosestToID(int id, boolean preferOlder) throws CurseException {
		return filesDirect().fileClosestToID(id, preferOlder);
	}

	public CurseFile recommendedFile() throws CurseException {
		return fileWithID(widgetInfo.download.id);
	}

	public TRLList<Relation> dependencies() throws CurseException {
		return dependencies(RelationType.ALL_TYPES);
	}

	public TRLList<Relation> dependencies(RelationType relationType) throws CurseException {
		if(!dependencies.containsKey(relationType)) {
			reloadDependencies(relationType);
		}

		return dependencies.get(relationType);
	}

	public void reloadDependencies(RelationType relationType) throws CurseException {
		reloadDependencies(relationType, null, null);
	}

	public void reloadDependencies(RelationType relationType,
			RunnableWithInput<Relation> onDependencyAdd, StopSwitch stopSwitch)
			throws CurseException {
		dependencies.put(relationType,
				getRelations("dependencies", relationType, onDependencyAdd, stopSwitch));
	}

	private TRLList<Relation> getRelations(String relationName, RelationType relationType,
			RunnableWithInput<Relation> onRelationAdd, StopSwitch stopSwitch)
			throws CurseException {
		String baseURL = urlString() + "/relations/" + relationName;

		if(relationType == RelationType.ALL_TYPES) {
			baseURL += "?";
		} else {
			baseURL += "?filter-related-" + relationName + "=" + relationType.ordinal() + "&";
		}

		return DocumentUtils.iteratePages(baseURL,
				(document, relations) -> documentToRelations(document, relations, relationType),
				onRelationAdd, stopSwitch, true);
	}

	private void documentToRelations(Element document, List<Relation> relations,
			RelationType relationType) throws CurseException {
		for(Element relation : document.getElementsByClass("project-list-item")) {
			final String projectURL =
					DocumentUtils.getValue(relation, "class=name-wrapper;tag=a;absUrl=href");
			//Some elements are empty for some reason
			if(!projectURL.isEmpty()) {
				relations.add(getRelationInfo(relation, URLUtils.url(projectURL), relationType));
			}
		}
	}

	private Relation getRelationInfo(Element element, URL url, RelationType relationType)
			throws CurseException {
		final String title = DocumentUtils.getValue(element, "class=name-wrapper;tag=a;text");
		final URL authorURL =
				URLUtils.url(DocumentUtils.getValue(element, "tag=span;tag=a;absUrl=href"));
		final String author = DocumentUtils.getValue(element, "tag=span;tag=a;text");
		final int downloads = Integer.parseInt(DocumentUtils.getValue(
				element, "class=e-download-count;text").replaceAll(",", ""));
		final long lastUpdateTime = Long.parseLong(
				DocumentUtils.getValue(element, "class=standard-date;attr=data-epoch"));
		final String shortDescription =
				DocumentUtils.getValue(element, "class=description;tag=p;text");
		final Category[] categories = getCategories(
				element.getElementsByClass("category-icons")).toArray(new Category[0]);

		return new Relation(url, title, authorURL, author, downloads, lastUpdateTime,
				shortDescription, categories, this, relationType);
	}

	private static TRLList<Category> getCategories(Elements categoryElements)
			throws CurseException {
		final TRLList<Category> categories = new TRLList<>();
		for(Element category : categoryElements) {
			final String name = DocumentUtils.getValue(category, "tag=a;attr=title");
			final URL url = URLUtils.url(DocumentUtils.getValue(category, "tag=a;absUrl=href"));
			final URL thumbnailURL =
					URLUtils.url(DocumentUtils.getValue(category, "tag=img;absUrl=src"));

			categories.add(new Category(name, url, thumbnailURL));
		}
		return categories;
	}

	public TRLList<Relation> dependents() throws CurseException {
		return dependents(RelationType.ALL_TYPES);
	}

	public TRLList<Relation> dependents(RelationType relationType) throws CurseException {
		if(!dependents.containsKey(relationType)) {
			reloadDependents(relationType);
		}

		return dependents.get(relationType);
	}

	public void reloadDependents(RelationType relationType) throws CurseException {
		reloadDependents(relationType, null, null);
	}

	public void reloadDependents(RelationType relationType,
			RunnableWithInput<Relation> onDependentAdd, StopSwitch stopSwitch)
			throws CurseException {
		dependents.put(relationType,
				getRelations("dependents", relationType, onDependentAdd, stopSwitch));
	}

	public void reloadURL() throws CurseException {
		url = CurseForge.fromID(id());
		mainCurseForgeURL = CurseForge.toMainCurseForgeProject(url);
	}

	public void reload(boolean useWidgetAPI) throws CurseException {
		site = CurseForgeSite.fromURL(url);
		if(site == null) {
			throw new CurseException("Could not find CurseForgeSite for URL: " + url);
		}

		if(CurseAPI.isAvoidingWidgetAPI() || !useWidgetAPI || mainCurseForgeURL == null) {
			final int id = CurseForge.getID(url);
			final Game game = site.game();
			final String type = DocumentUtils.getValue(url, "tag=title;text").split(" - ")[2];

			final URLInfo urls = new URLInfo();
			urls.project = url;
			urls.curseforge = null;

			final String title =
					DocumentUtils.getValue(url, "class=project-title;class=overflow-tip;text");

			URL donate = null;

			try {
				donate = URLUtils.url(
						DocumentUtils.getValue(url, "class=icon-donate;attr=href;absUrl=href"));
			} catch(CurseException ignored) {}

			final String license = DocumentUtils.getValue(url, "class=info-data=4;tag=a;text");

			final List<MemberInfo> memberInfos = new ArrayList<>();
			final Elements projectMembers =
					DocumentUtils.get(url).getElementsByClass("project-members");

			for(Element member : projectMembers) {
				final MemberInfo memberInfo = new MemberInfo();

				memberInfo.title =
						MemberType.fromName(DocumentUtils.getValue(member, "class=title;text"));
				memberInfo.username = DocumentUtils.getValue(member, "tag=span;text");

				memberInfos.add(memberInfo);
			}

			final MemberInfo[] members = memberInfos.toArray(new MemberInfo[0]);

			final DownloadsInfo downloads = new DownloadsInfo();
			downloads.total = Integer.parseInt(
					DocumentUtils.getValue(url, "class=info-data=3;text").replaceAll(",", ""));

			URL thumbnail;
			try {
				thumbnail = URLUtils.url(
						DocumentUtils.getValue(url, "class=e-avatar64;tag=img;absUrl=src"));
			} catch(CurseException ex) {
				thumbnail = PLACEHOLDER_THUMBNAIL;
			}

			final String createdAt = DocumentUtils.getValue(url,
					"class=standard-date;attr=data-epoch");
			final String description = DocumentUtils.getValue(url, "tag=meta=5;attr=content");
			final String lastFetch = Long.toString(Instant.now().getEpochSecond());

			widgetInfo = new ProjectInfo(id, game, type, urls, title, donate, license, members,
					downloads, thumbnail, createdAt, description, lastFetch);
			widgetInfo.retrievedDirectly = false;

			if(CurseAPI.isAvoidingWidgetAPI()) {
				widgetInfo.failedToRetrieveDirectly = true;
			}
		} else {
			try {
				widgetInfo = WidgetAPI.get(mainCurseForgeURL.getPath());
			} catch(CurseException ex) {
				if(mainCurseForgeURL == null) {
					ThrowableHandling.handle(ex);
				}

				ThrowableHandling.handleWithoutExit(ex);
				reload(false);
				widgetInfo.failedToRetrieveDirectly = true;
				return;
			}

			//So == can be used
			if(widgetInfo.thumbnail.equals(PLACEHOLDER_THUMBNAIL)) {
				widgetInfo.thumbnail = PLACEHOLDER_THUMBNAIL;
			}
		}

		//Retrieving categories
		this.categories = getCategories(DocumentUtils.get(url).
				getElementsByClass("project-categories").get(0).
				getElementsByTag("li")).toImmutableList();
	}

	public ProjectInfo widgetInfo() {
		return widgetInfo.clone();
	}

	public void reloadFiles() throws CurseException {
		reloadFiles(0);
	}

	public void reloadFiles(int oldestNecessaryID) throws CurseException {
		reloadFiles(false, oldestNecessaryID);
	}

	public void reloadFiles(boolean forceReloadWidgetInfo, int oldestNecessaryID)
			throws CurseException {
		final List<CurseFile> files;

		if(forceReloadWidgetInfo ||
				(!widgetInfo.retrievedDirectly && !widgetInfo.failedToRetrieveDirectly)) {
			reload(true);
		}

		if(widgetInfo.failedToRetrieveDirectly) {
			files = DocumentUtils.iteratePages(url + "/files?", this::getFiles, null, null, true);

			widgetInfo.files = new FileInfo[files.size()];
			for(int i = 0; i < files.size(); i++) {
				widgetInfo.files[i] = files.get(i).widgetInfo();
			}

			widgetInfo.download = widgetInfo.files.length == 0 ?
					null : DownloadInfo.fromFileInfo(widgetInfo.files[0]);
		} else {
			files = new ArrayList<>(widgetInfo.versions.size());
			for(Map.Entry<String, FileInfo[]> entry : widgetInfo.versions.entrySet()) {
				for(FileInfo info : entry.getValue()) {
					files.add(new CurseFile(this, info));
				}
			}
			files.sort((file1, file2) -> Integer.compare(file2.id(), file1.id()));
		}

		this.files = CurseFileList.of(files);
	}

	private void getFiles(Element document, List<CurseFile> files) throws CurseException {
		try {
			for(Element file : document.getElementsByClass("project-file-list-item")) {
				final int id = Integer.parseInt(ArrayUtils.last(DocumentUtils.getValue(
						file, "class=twitch-link;attr=href").split("/")));

				final URL url = URLUtils.url(DocumentUtils.getValue(
						file, "class=twitch-link;attr=href;absUrl=href"));

				final String name = DocumentUtils.getValue(file, "class=twitch-link;text");

				//<div class="alpha-phase tip">
				final ReleaseType type = ReleaseType.fromName(DocumentUtils.getValue(file,
						"class=project-file-release-type;class=tip;class").
						split("-")[0]);

				final String[] versions;
				if(file.getElementsByClass("additional-versions").isEmpty()) {
					versions = new String[] {
							DocumentUtils.getValue(file, "class=version-label;text")
					};
				} else {
					String value =
							DocumentUtils.getValue(file, "class=additional-versions;attr=title");
					value = value.substring(5, value.length() - 6);
					versions = value.split("</div><div>");
				}

				final String fileSize =
						DocumentUtils.getValue(file, "class=project-file-size;text");

				final int downloads = Integer.parseInt(
						DocumentUtils.getValue(file, "class=project-file-downloads;text").
								replaceAll(",", ""));

				final String uploadedAt = DocumentUtils.getValue(file,
						"class=standard-date;attr=data-epoch");

				files.add(new CurseFile(CurseProject.this, new FileInfo(
						id, url, name, type, versions, fileSize, downloads, uploadedAt)));
			}
		} catch(NullPointerException | NumberFormatException ex) {
			throw new CurseException(ex);
		}
	}

	public void clearRelationCache() {
		clearDependencyCache();
		clearDependentCache();
	}

	public void clearDependencyCache() {
		dependencies.clear();
	}

	public void clearDependentCache() {
		dependents.clear();
	}

	public void clearDependencyCache(RelationType relationType) {
		dependencies.remove(relationType);
	}

	public void clearDependentCache(RelationType relationType) {
		dependents.remove(relationType);
	}

	@Override
	public int hashCode() {
		return id();
	}

	@Override
	public boolean equals(Object anotherObject) {
		if(anotherObject instanceof CurseProject) {
			return ((CurseProject) anotherObject).id() == id();
		}

		return false;
	}

	@Override
	public String toString() {
		return getClass().getName() +
				"[id=" + id() + ",title=" + title() + ",game=" + game() + "]";
	}

	public String title() {
		return widgetInfo.title;
	}

	public Game game() {
		return widgetInfo.game;
	}
}
