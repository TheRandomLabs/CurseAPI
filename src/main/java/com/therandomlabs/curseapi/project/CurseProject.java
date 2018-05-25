package com.therandomlabs.curseapi.project;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
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
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFileList;
import com.therandomlabs.curseapi.file.ReleaseType;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.MiscUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.curseapi.widget.FileInfo;
import com.therandomlabs.curseapi.widget.MemberInfo;
import com.therandomlabs.curseapi.widget.ProjectInfo;
import com.therandomlabs.curseapi.widget.WidgetAPI;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.io.NetUtils;
import com.therandomlabs.utils.throwable.ThrowableHandling;
import com.therandomlabs.utils.wrapper.Wrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//TODO Images, Issues, Source, Pages, Wiki, get number of relations, get relations on specific pages
public final class CurseProject {
	public static final CurseProject NULL_PROJECT = new CurseProject();

	private static final Map<Integer, CurseProject> projects = new ConcurrentHashMap<>();

	private final Map<RelationType, TRLList<Relation>> dependencies = new ConcurrentHashMap<>();
	private final Map<RelationType, TRLList<Relation>> dependents = new ConcurrentHashMap<>();

	private BufferedImage avatar;

	private URL url;
	private URL mainCurseForgeURL;
	private CurseForgeSite site;
	private Element document;

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

	private final Map<URL, WeakReference<Document>> documentCache = new ConcurrentHashMap<>();

	private final boolean curseMeta;

	private CurseFileList files;

	//Incomplete list of files used as a cache
	private final CurseFileList incompleteFiles = new CurseFileList();

	private boolean avoidWidgetAPI = CurseAPI.isAvoidingWidgetAPI();
	private boolean avoidCurseMeta = CurseAPI.isAvoidingCurseMeta();

	private CurseProject() {
		curseMeta = false;
		site = CurseForgeSite.UNKNOWN;
		shortDescription = "Null project";
		descriptionHTML = Jsoup.parse("Null project");
		description = "Null project";
		game = Game.UNKNOWN;
		type = ProjectType.UNKNOWN;
		categories = new ImmutableList<>(Category.UNKNOWN);
		avatarURL = CurseAPI.PLACEHOLDER_THUMBNAIL_URL;
		avatarURLString = CurseAPI.PLACEHOLDER_THUMBNAIL_URL_STRING;
		thumbnailURL = CurseAPI.PLACEHOLDER_THUMBNAIL_URL;
		thumbnailURLString = CurseAPI.PLACEHOLDER_THUMBNAIL_URL_STRING;
		members = new ImmutableList<>(Member.UNKNOWN);
		creationTime = ZonedDateTime.now();
		licenseName = "Unknown License";
		licenseHTML = Jsoup.parse("Unknown license");
		license = "Unknown license";
		files = new CurseFileList();
	}

	private CurseProject(int id) throws CurseException {
		this(CurseForge.fromID(id));
	}

	private CurseProject(Map.Entry<URL, Document> project) throws CurseException {
		this.url = project.getKey();
		this.document = project.getValue();
		curseMeta = false;
		this.mainCurseForgeURL = CurseForge.toMainCurseForgeProject(document);

		reload(false);

		projects.put(id, this);
	}

	/* CurseMeta constructor - not needed ATM, but might be in the future
	private CurseProject(int id, boolean curseMeta) throws CurseException {
		this.curseMeta = curseMeta;
		avoidWidgetAPI = true;
		avoidCurseMeta = false;

		this.id = id;

		reload();

		projects.put(id, this);
	}
	*/

	public boolean isNull() {
		return site == CurseForgeSite.UNKNOWN && game == Game.UNKNOWN &&
				type == ProjectType.UNKNOWN;
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
				avatar = ImageIO.read(NetUtils.download(avatarURL));
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
		return ImageIO.read(NetUtils.download(thumbnailURL()));
	}

	public Member owner() {
		return members(MemberType.OWNER).get(0);
	}

	public String ownerUsername() {
		return owner().username();
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

			DocumentUtils.putTemporaryCache(this, documentCache);
			final List<CurseFile> files = DocumentUtils.iteratePages(
					this,
					url + "/files?",
					this::getFiles,
					file -> {
						if(predicate.test(file)) {
							latestFile.set(file);
							return false;
						}
						return true;
					},
					false
			);
			DocumentUtils.removeTemporaryCache(this);

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
		return latestFile(CollectionUtils.toStrings(MinecraftVersion.getVersions(versions)));
	}

	public CurseFile latestFile(ReleaseType minimumStability, MinecraftVersion... versions)
			throws CurseException {
		return latestFile(CollectionUtils.toStrings(MinecraftVersion.getVersions(versions)),
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
			DocumentUtils.putTemporaryCache(this, documentCache);
			final List<CurseFile> files = DocumentUtils.iteratePages(
					this,
					url + "/files?",
					this::getFiles,
					file -> file.id() >= oldID, //Continue as long as file.ID() >= oldID
					false
			);
			DocumentUtils.removeTemporaryCache(this);

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

		if(avoidCurseMeta) {
			if(avoidWidgetAPI) {
				final Wrapper<CurseFile> fileWithID = new Wrapper<>();

				DocumentUtils.putTemporaryCache(this, documentCache);
				incompleteFiles.addAll(DocumentUtils.iteratePages(
						this,
						url + "/files?",
						this::getFiles,
						file -> {
							if(file.id() == id) {
								fileWithID.set(file);
								return false;
							}

							return file.id() > id;
						},
						false
				));
				DocumentUtils.removeTemporaryCache(this);

				if(fileWithID.hasValue()) {
					return fileWithID.get();
				}
			} else {
				final CurseFile file = filesDirect().fileWithID(id);

				if(file != null) {
					return file;
				}
			}

			return CurseFile.nullFile(this.id, id);
		}

		return new CurseFile(this.id, CurseMeta.getFile(this.id, id));
	}

	public CurseFile fileClosestToID(int id, boolean preferOlder) throws CurseException {
		if(avoidCurseMeta) {
			if(avoidWidgetAPI) {
				DocumentUtils.putTemporaryCache(this, documentCache);
				incompleteFiles.addAll(DocumentUtils.iteratePages(
						this,
						url + "/files?",
						this::getFiles,
						file -> file.id() > id - 1,
						false
				));
				DocumentUtils.removeTemporaryCache(this);

				return incompleteFiles.fileClosestToID(id, preferOlder);
			}

			return filesDirect().fileClosestToID(id, preferOlder);
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
		reloadDependencies(relationType, null);
	}

	public void reloadDependencies(RelationType relationType, Predicate<Relation> onDependencyAdd)
			throws CurseException {
		dependencies.put(relationType, getRelations("dependencies", relationType, onDependencyAdd));
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
		reloadDependents(relationType, null);
	}

	public void reloadDependents(RelationType relationType, Predicate<Relation> onDependentAdd)
			throws CurseException {
		dependents.put(relationType, getRelations("dependents", relationType, onDependentAdd));
	}

	private TRLList<Relation> getRelations(String relationName, RelationType relationType,
			Predicate<Relation> onRelationAdd) throws CurseException {
		String baseURL = urlString() + "/relations/" + relationName;

		if(relationType == RelationType.ALL_TYPES) {
			baseURL += "?";
		} else {
			baseURL += "?filter-related-" + relationName + "=" + relationType.ordinal() + "&";
		}

		DocumentUtils.putTemporaryCache(this, documentCache);
		final TRLList<Relation> relationList = DocumentUtils.iteratePages(
				this,
				baseURL,
				(document, relations) -> documentToRelations(document, relations, relationType),
				onRelationAdd,
				true
		);
		DocumentUtils.removeTemporaryCache(this);

		return relationList;
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
		final Map.Entry<URL, Document> project = CurseForge.fromID(id);
		url = project.getKey();
		document = project.getValue();
		mainCurseForgeURL = CurseForge.toMainCurseForgeProject(url);
	}

	public void reload() throws CurseException {
		reload(false);
	}

	private void reload(boolean useWidgetAPI) throws CurseException {
		if(isNull()) {
			return;
		}

		if(curseMeta) {
			reloadCurseMeta();
		}

		site = CurseForgeSite.fromURL(url);

		if(avoidWidgetAPI || !useWidgetAPI || mainCurseForgeURL == null) {
			id = CurseForge.getID(document);
			title = DocumentUtils.getValue(document, "class=project-title;class=overflow-tip;text");
			shortDescription = DocumentUtils.getValue(document, "name=description=1;attr=content");
			game = site.game();
			type = ProjectType.get(site,
					DocumentUtils.getValue(document, "tag=title;text").split(" - ")[2]);

			try {
				thumbnailURLString =
						DocumentUtils.getValue(document, "class=e-avatar64;tag=img;absUrl=src");
				thumbnailURL = URLUtils.url(thumbnailURLString);
			} catch(CurseException ex) {
				thumbnailURLString = CurseAPI.PLACEHOLDER_THUMBNAIL_URL_STRING;
				thumbnailURL = CurseAPI.PLACEHOLDER_THUMBNAIL_URL;
			}

			members.clear();
			for(Element member : document.getElementsByClass("project-members")) {
				members.add(new Member(
						MemberType.fromName(DocumentUtils.getValue(member, "class=title;text")),
						DocumentUtils.getValue(member, "tag=span;text")
				));
			}

			downloads = Integer.parseInt(
					DocumentUtils.getValue(document, "class=info-data=3;text").replaceAll(",", ""));
			creationTime = MiscUtils.parseTime(DocumentUtils.getValue(document,
					"class=project-details;class=standard-date;attr=data-epoch"));

			try {
				donateURLString =
						DocumentUtils.getValue(document, "class=icon-donate;attr=href;absUrl=href");
			} catch(CurseException ignored) {}

			donateURL = donateURLString == null ? null : URLUtils.url(donateURLString);
			licenseName = DocumentUtils.getValue(document, "class=info-data=4;tag=a;text");
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

			members = new TRLList<>(info.members.length);
			for(MemberInfo member : info.members) {
				members.add(new Member(member.title, member.username));
			}

			downloads = info.downloads.total;
			creationTime = MiscUtils.parseTime(info.created_at);
			donateURL = info.donate;
			donateURLString = donateURL == null ? null : donateURLString;
			licenseName = info.license;
			widgetInfoFiles = info.versions;
		}

		descriptionHTML = DocumentUtils.get(document, "class=project-description");
		description = DocumentUtils.getPlainText(descriptionHTML);
		categories = getCategories(
				document.getElementsByClass("project-categories").get(0).
				getElementsByTag("li")
		).toImmutableList();
		avatarURLString = DocumentUtils.getValue(document, "class=e-avatar64;absUrl=href");
		avatarURL = avatarURLString.isEmpty() ?
				CurseAPI.PLACEHOLDER_THUMBNAIL_URL : URLUtils.url(avatarURLString);

		//So it can be garbage collected
		document = null;
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
		if(isNull()) {
			return;
		}

		if(avoidCurseMeta) {
			if(avoidWidgetAPI) {
				DocumentUtils.putTemporaryCache(this, documentCache);
				this.files = new CurseFileList(DocumentUtils.iteratePages(
						this,
						url + "/files?",
						this::getFiles,
						null,
						true
				));
				DocumentUtils.removeTemporaryCache(this);

				return;
			}

			final TRLList<CurseFile> files = new TRLList<>(widgetInfoFiles.size() * 10);

			for(Map.Entry<String, FileInfo[]> entry : widgetInfoFiles.entrySet()) {
				for(FileInfo info : entry.getValue()) {
					files.add(new CurseFile(this, info));
				}
			}

			this.files = new CurseFileList(files);
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
		CurseProject project = projects.get(id);
		if(project != null) {
			return project;
		}

		try {
			return new CurseProject(id);
		} catch(InvalidProjectIDException ignored) {
			/*try {
				return new CurseProject(id, true);
			} catch(CurseMetaException ex2) {
				throw ex;
			}*/
		}

		project = nullProject(id);
		projects.put(id, project);
		return project;
	}

	public static CurseProject fromURL(URL url) throws CurseException {
		return fromURL(url, false);
	}

	public static CurseProject fromURL(URL url, boolean followRedirections) throws CurseException {
		if(followRedirections) {
			url = URLUtils.redirect(url);
		}

		for(CurseProject project : projects.values()) {
			if(url.equals(project.url)) {
				return project;
			}
		}

		return new CurseProject(new AbstractMap.SimpleEntry<>(url, DocumentUtils.get(url)));
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

	public static CurseProject nullProject(int id) {
		if(id == 0) {
			return NULL_PROJECT;
		}

		final CurseProject project = new CurseProject();
		project.id = id;
		return project;
	}

	public static void clearProjectCache() {
		projects.clear();
	}

	public static boolean isCached(int id) {
		return projects.containsKey(id);
	}
}
