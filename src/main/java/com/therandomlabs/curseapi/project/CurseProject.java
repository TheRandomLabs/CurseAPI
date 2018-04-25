package com.therandomlabs.curseapi.project;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.Game;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.curseapi.curseforge.CurseForgeSite;
import com.therandomlabs.curseapi.cursemeta.AddOn;
import com.therandomlabs.curseapi.cursemeta.CurseMeta;
import com.therandomlabs.curseapi.cursemeta.CurseMetaException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFileList;
import com.therandomlabs.curseapi.file.ReleaseType;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.MiscUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.curseapi.widget.FileInfo;
import com.therandomlabs.curseapi.widget.ProjectInfo;
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

//TODO Images, Issues, Source, Pages, Wiki, get number of relations, get relations on specific pages
public final class CurseProject {
	private static final Map<Integer, CurseProject> projects = new ConcurrentHashMap<>();

	private final Map<RelationType, TRLList<Relation>> dependencies = new ConcurrentHashMap<>();
	private final Map<RelationType, TRLList<Relation>> dependents = new ConcurrentHashMap<>();

	private BufferedImage avatar;

	private URL url;
	private URL mainCurseForgeURL;
	private CurseForgeSite site;

	private int id;
	private String title;
	private String shortDescription;
	private Element descriptionHTML;
	private String description;
	private Game game;
	private ProjectType type;
	private TRLList<Category> categories;
	private URL avatarURL;
	private String avatarURLString;
	private URL thumbnailURL;
	private String thumbnailURLString;
	private TRLList<Member> members = new TRLList<>();
	private int downloads;
	private ZonedDateTime creationTime;
	private String licenseName;
	private Element licenseHTML;
	private String license;
	private URL donateURL;
	private String donateURLString;

	private Map<String, FileInfo[]> widgetInfoFiles;

	private final boolean curseMeta;

	private CurseFileList files;

	//Incomplete list of files used as a cache
	private final CurseFileList incompleteFiles = new CurseFileList();

	private boolean avoidWidgetAPI = CurseAPI.isAvoidingWidgetAPI();
	private boolean avoidCurseMeta = CurseAPI.isAvoidingCurseMeta();

	private CurseProject(int id) throws CurseException {
		this(CurseForge.fromID(id));
	}

	private CurseProject(URL url) throws CurseException {
		CurseException.validateProject(url);

		curseMeta = false;
		this.url = url;
		this.mainCurseForgeURL = CurseForge.toMainCurseForgeProject(url);
		reload(false);

		projects.put(id, this);
	}

	private CurseProject(int id, boolean curseMeta) throws CurseException {
		this.curseMeta = curseMeta;
		avoidWidgetAPI = true;
		avoidCurseMeta = false;

		this.id = id;

		reload();

		projects.put(id, this);
	}

	public int id() {
		return id;
	}

	public String title() {
		return title;
	}

	public Game game() {
		return game;
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
		return type;
	}

	public CurseForgeSite site() {
		return site;
	}

	public URL avatarURL() {
		return avatarURL;
	}

	public String avatarURLString() {
		return avatarURLString;
	}

	public BufferedImage avatar() throws IOException {
		if(avatar == null) {
			if(avatarURL == CurseAPI.PLACEHOLDER_THUMBNAIL_URL) {
				avatar = CurseAPI.getPlaceholderThumbnail();
			} else {
				avatar = ImageIO.read(NetworkUtils.download(avatarURL));
			}
		}

		return avatar;
	}

	public URL thumbnailURL() {
		return thumbnailURL;
	}

	public String thumbnailURLString() {
		return thumbnailURLString;
	}

	public BufferedImage thumbnail() throws IOException {
		return ImageIO.read(NetworkUtils.download(thumbnailURL()));
	}

	public Member owner() {
		return members(MemberType.OWNER).get(0);
	}

	public TRLList<Member> members(MemberType type) {
		final TRLList<Member> membersWithType = new TRLList<>(members.size());
		for(Member member : members) {
			if(member.type() == type) {
				membersWithType.add(member);
			}
		}
		return membersWithType;
	}

	public TRLList<Member> members() {
		return members.clone();
	}

	public int downloads() {
		return downloads;
	}

	public ZonedDateTime creationTime() {
		return creationTime;
	}

	public ZonedDateTime lastUpdateTime() throws CurseException {
		return latestFile().uploadTime();
	}

	public URL donateURL() {
		return donateURL;
	}

	public String donateURLString() {
		return donateURLString;
	}

	public String licenseName() {
		return licenseName;
	}

	public String licenseText() throws CurseException {
		licenseHTML();
		return license;
	}

	public Element licenseHTML() throws CurseException {
		if(licenseHTML == null) {
			licenseHTML = DocumentUtils.get(url + "/license");
			license = DocumentUtils.getPlainText(licenseHTML);
		}

		return licenseHTML;
	}

	public String urlString() {
		return url.toString();
	}

	public String shortDescription() {
		return shortDescription;
	}

	public String description() throws CurseException {
		descriptionHTML();
		return description;
	}

	public Element descriptionHTML() throws CurseException {
		if(curseMeta && descriptionHTML == null) {
			descriptionHTML = CurseMeta.getDescription(id);
			description = DocumentUtils.getPlainText(descriptionHTML);
		}

		return descriptionHTML;
	}

	public TRLList<Category> categories() {
		return categories;
	}

	public boolean isAvoidingWidgetAPI() {
		return avoidWidgetAPI;
	}

	public void avoidWidgetAPI(boolean flag) {
		if(!curseMeta) {
			avoidWidgetAPI = flag;
		}
	}

	public boolean isAvoidingCurseMeta() {
		return avoidCurseMeta;
	}

	public void avoidCurseMeta(boolean flag) {
		if(!curseMeta) {
			avoidCurseMeta = flag;
		}
	}

	public CurseFile latestFile() throws CurseException {
		if(!incompleteFiles.isEmpty()) {
			return incompleteFiles.latest();
		}

		if(avoidWidgetAPI && avoidCurseMeta) {
			final List<CurseFile> files = new TRLList<>();

			getFiles(DocumentUtils.get(url + "/files?page=1"), files);

			//Add to cache
			incompleteFiles.addAll(files);

			return files.get(0);
		}

		return filesDirect().latest();
	}

	public CurseFile latestFile(Predicate<CurseFile> predicate) throws CurseException {
		if(!incompleteFiles.isEmpty()) {
			final CurseFile file = incompleteFiles.latest(predicate);
			if(file != null) {
				return file;
			}
		}

		if(avoidWidgetAPI && avoidCurseMeta) {
			final Wrapper<CurseFile> latestFile = new Wrapper<>();
			final StopSwitch stopSwitch = new StopSwitch();

			final List<CurseFile> files = DocumentUtils.iteratePages(
					url + "/files?",
					this::getFiles,
					file -> {
						if(!stopSwitch.isStopped() && predicate.test(file)) {
							latestFile.set(file);
							stopSwitch.stop();
						}
					},
					stopSwitch,
					false
			);

			incompleteFiles.addAll(files);

			return latestFile.get();
		}

		return filesDirect().latest(predicate);
	}

	public CurseFile latestFile(Collection<String> versions) throws CurseException {
		return latestFile(file -> file.gameVersions().containsAny(versions));
	}

	public CurseFile latestFile(Collection<String> versions, ReleaseType minimumStability)
			throws CurseException {
		return latestFile(file -> file.gameVersions().containsAny(versions) &&
				file.matchesMinimumStability(minimumStability));
	}

	public CurseFile latestFile(String... versions) throws CurseException {
		return latestFile(new ImmutableList<>(versions));
	}

	public CurseFile latestFile(ReleaseType minimumStability, String... versions)
			throws CurseException {
		return latestFile(new ImmutableList<>(versions), minimumStability);
	}

	public CurseFile latestFile(MinecraftVersion... versions) throws CurseException {
		return latestFile(CollectionUtils.stringify(MinecraftVersion.getVersions(versions)));
	}

	public CurseFile latestFile(ReleaseType minimumStability, MinecraftVersion... versions)
			throws CurseException {
		return latestFile(CollectionUtils.stringify(MinecraftVersion.getVersions(versions)),
				minimumStability);
	}

	public CurseFile latestFileWithMCVersionGroup(String version) throws CurseException {
		return latestFile(MinecraftVersion.groupFromString(version));
	}

	public CurseFile latestFileWithMCVersionGroup(String version, ReleaseType minimumStability)
			throws CurseException {
		return latestFile(minimumStability, MinecraftVersion.groupFromString(version));
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

	public CurseFileList filesBetween(int oldID, int newID) throws CurseException {
		if(incompleteFiles.hasFileOlderThan(oldID) &&
				incompleteFiles.hasFileNewerThanOrEqualTo(newID)) {
			final CurseFileList files = incompleteFiles.clone();
			files.between(oldID, newID);
			return files;
		}

		if(avoidWidgetAPI && avoidCurseMeta) {
			final StopSwitch stopSwitch = new StopSwitch();

			final List<CurseFile> files = DocumentUtils.iteratePages(
					url + "/files?",
					this::getFiles,
					file -> {
						if(file.id() <= oldID) {
							stopSwitch.stop();
						}
					},
					stopSwitch,
					false
			);

			//Add to cache
			incompleteFiles.addAll(files);

			final CurseFileList fileList = new CurseFileList(files);
			fileList.between(oldID, newID);
			return fileList;
		}

		final CurseFileList files = files();
		files.between(oldID, newID);
		return files;
	}

	public CurseFile fileWithID(int id) throws CurseException {
		if(!incompleteFiles.isEmpty()) {
			final CurseFile file = incompleteFiles.fileWithID(id);
			if(file != null) {
				return file;
			}
		}

		boolean useCurseMeta = false;

		if(avoidWidgetAPI && avoidCurseMeta) {
			final Wrapper<CurseFile> fileWithID = new Wrapper<>();
			final StopSwitch stopSwitch = new StopSwitch();

			incompleteFiles.addAll(DocumentUtils.iteratePages(
					url + "/files?",
					this::getFiles,
					file -> {
						if(file.id() == id) {
							fileWithID.set(file);
						} else if(file.id() < id) {
							stopSwitch.stop();
						}
					},
					stopSwitch,
					false
			));

			if(fileWithID.hasValue()) {
				return fileWithID.get();
			}

			useCurseMeta = true;
		}

		if(!useCurseMeta) {
			final CurseFile file = filesDirect().fileWithID(id);
			if(file != null) {
				return file;
			}
		}

		return CurseFile.fromID(this.id, id);
	}

	public CurseFile fileClosestToID(int id, boolean preferOlder) throws CurseException {
		final CurseFile fileWithID = fileWithID(id);
		if(fileWithID != null) {
			return fileWithID;
		}

		if(avoidWidgetAPI && avoidCurseMeta) {
			final StopSwitch stopSwitch = new StopSwitch();

			//Cache files up to the first file older than the file with the specified ID
			incompleteFiles.addAll(DocumentUtils.iteratePages(
					url + "/files?",
					this::getFiles,
					file -> {
						if(file.id() < id) {
							stopSwitch.stop();
						}
					},
					stopSwitch,
					false
			));

			return incompleteFiles.fileClosestToID(id, preferOlder);
		}

		return filesDirect().fileClosestToID(id, preferOlder);
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

	//TODO Function<Relation, Boolean> - if return value is true, stop
	public void reloadDependencies(RelationType relationType,
			RunnableWithInput<Relation> onDependencyAdd, StopSwitch stopSwitch)
			throws CurseException {
		dependencies.put(relationType,
				getRelations("dependencies", relationType, onDependencyAdd, stopSwitch));
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
		final String author = DocumentUtils.getValue(element, "tag=span;tag=a;text");
		final int downloads = Integer.parseInt(DocumentUtils.getValue(
				element, "class=e-download-count;text").replaceAll(",", ""));
		final long lastUpdateTime = Long.parseLong(
				DocumentUtils.getValue(element, "class=standard-date;attr=data-epoch"));
		final String shortDescription =
				DocumentUtils.getValue(element, "class=description;tag=p;text");
		final Category[] categories = getCategories(
				element.getElementsByClass("category-icons")).toArray(new Category[0]);

		return new Relation(url, title, author, downloads, lastUpdateTime,
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

	public void reloadURL() throws CurseException {
		url = CurseForge.fromID(id);
		mainCurseForgeURL = CurseForge.toMainCurseForgeProject(url);
	}

	public void reload() throws CurseException {
		reload(false);
	}

	private void reload(boolean useWidgetAPI) throws CurseException {
		if(curseMeta) {
			reloadCurseMeta();
		}

		site = CurseForgeSite.fromURL(url);

		if(avoidWidgetAPI || !useWidgetAPI || mainCurseForgeURL == null) {
			id = CurseForge.getID(url);
			title = DocumentUtils.getValue(url, "class=project-title;class=overflow-tip;text");
			shortDescription = DocumentUtils.getValue(url, "name=description=1;attr=content");
			game = site.game();
			type = ProjectType.get(site,
					DocumentUtils.getValue(url, "tag=title;text").split(" - ")[2]);

			try {
				thumbnailURLString =
						DocumentUtils.getValue(url, "class=e-avatar64;tag=img;absUrl=src");
				thumbnailURL = URLUtils.url(thumbnailURLString);
			} catch(CurseException ex) {
				thumbnailURLString = CurseAPI.PLACEHOLDER_THUMBNAIL_URL_STRING;
				thumbnailURL = CurseAPI.PLACEHOLDER_THUMBNAIL_URL;
			}

			members.clear();
			for(Element member : DocumentUtils.get(url).getElementsByClass("project-members")) {
				members.add(new Member(
						MemberType.fromName(DocumentUtils.getValue(member, "class=title;text")),
						DocumentUtils.getValue(member, "tag=span;text")
				));
			}

			downloads = Integer.parseInt(
					DocumentUtils.getValue(url, "class=info-data=3;text").replaceAll(",", ""));
			creationTime = MiscUtils.parseTime(DocumentUtils.getValue(url,
					"class=project-details;class=standard-date;attr=data-epoch"));

			try {
				donateURLString =
						DocumentUtils.getValue(url, "class=icon-donate;attr=href;absUrl=href");
			} catch(CurseException ignored) {}

			donateURL = donateURLString == null ? null : URLUtils.url(donateURLString);
			licenseName = DocumentUtils.getValue(url, "class=info-data=4;tag=a;text");
		} else {
			if(mainCurseForgeURL == null) {
				avoidWidgetAPI = true;
				reload(false);
				return;
			}

			ProjectInfo info;

			try {
				info = WidgetAPI.get(mainCurseForgeURL.getPath());
			} catch(CurseException ex) {
				ThrowableHandling.handleWithoutExit(ex);
				avoidWidgetAPI = true;
				reload(false);
				return;
			}

			id = info.id;
			title = info.title;
			shortDescription = info.description;
			game = info.game;
			type = ProjectType.get(site, info.type);
			thumbnailURL = info.thumbnail;
			thumbnailURLString = thumbnailURL.toString();
			members = Arrays.stream(info.members).
					map(member -> new Member(member.title, member.username)).
					collect(TRLCollectors.toArrayList());
			downloads = info.downloads.total;
			creationTime = MiscUtils.parseTime(info.created_at);
			donateURL = info.donate;
			donateURLString = donateURL == null ? null : donateURLString;
			licenseName = info.license;
			widgetInfoFiles = info.versions;
		}

		descriptionHTML = DocumentUtils.get(url, "class=project-description");
		description = DocumentUtils.getPlainText(descriptionHTML);
		categories = getCategories(
				DocumentUtils.get(url).
						getElementsByClass("project-categories").get(0).
						getElementsByTag("li")
		).toImmutableList();
		avatarURLString = DocumentUtils.getValue(url, "class=e-avatar64;absUrl=href");
		avatarURL = avatarURLString.isEmpty() ?
				CurseAPI.PLACEHOLDER_THUMBNAIL_URL : URLUtils.url(avatarURLString);
	}

	private void reloadCurseMeta() throws CurseException {
		final AddOn addon = CurseMeta.getAddOn(id);

		id = addon.Id;
		title = addon.Name;
		game = Game.fromID(addon.GameId);
		url = URLUtils.redirect(CurseForge.URL + "projects/" + id);
		site = CurseForgeSite.UNKNOWN;
		mainCurseForgeURL = addon.WebSiteURL;
		avatarURL = addon.AvatarUrl == null ? CurseAPI.PLACEHOLDER_THUMBNAIL_URL : addon.AvatarUrl;
		avatarURLString = avatarURL.toString();
		thumbnailURL = avatarURL;
		thumbnailURLString = avatarURLString;
		members = new TRLList<>(Member.fromAuthors(addon.Authors));
		downloads = (int) addon.DownloadCount;
		creationTime = null;
		donateURL = addon.DonationUrl;
		donateURLString = donateURL == null ? null : donateURL.toString();
		shortDescription = addon.Summary;
		categories = new TRLList<>(Category.fromAddOnCategories(addon.Categories));
	}

	public void reloadFiles() throws CurseException {
		if(!avoidWidgetAPI && avoidCurseMeta) {
			reload(true);

			final TRLList<CurseFile> files = new TRLList<>(widgetInfoFiles.size() * 10);

			for(Map.Entry<String, FileInfo[]> entry : widgetInfoFiles.entrySet()) {
				for(FileInfo info : entry.getValue()) {
					files.add(new CurseFile(this, info));
				}
			}

			this.files = new CurseFileList(files);
			return;
		}

		if(avoidCurseMeta) {
			this.files = new CurseFileList(
					DocumentUtils.iteratePages(url + "/files?", this::getFiles, null, null, true));
			return;
		}

		files = CurseFile.filesFromProjectID(id);
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
			throw CurseException.fromThrowable(ex);
		}
	}

	public void clearAvatarCache() {
		avatar = null;
	}

	public void clearLicenseCache() {
		licenseHTML = null;
		license = null;
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

	public void clearRelationCache() {
		clearDependencyCache();
		clearDependentCache();
	}

	public void clearCache() {
		clearAvatarCache();
		clearLicenseCache();
		clearRelationCache();
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
		return getClass().getName() + "[id=" + id() + ",title=" + title() + ",game=" + game() + "]";
	}

	public static CurseProject fromID(String id) throws CurseException {
		return fromID(Integer.parseInt(id));
	}

	public static CurseProject fromID(int id) throws CurseException {
		final CurseProject project = projects.get(id);
		if(projects.containsKey(id)) {
			if(project == null) {
				throw new InvalidProjectIDException(id);
			}

			return project;
		}

		try {
			return new CurseProject(id);
		} catch(InvalidProjectIDException ex) {
			try {
				projects.put(id, null);
				return new CurseProject(id, true);
			} catch(CurseMetaException ex2) {
				throw ex;
			}
		}
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
}
