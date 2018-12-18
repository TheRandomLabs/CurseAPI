package com.therandomlabs.curseapi.project;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseForge;
import com.therandomlabs.curseapi.CurseForgeSite;
import com.therandomlabs.curseapi.InvalidCurseForgeProjectException;
import com.therandomlabs.curseapi.RelationType;
import com.therandomlabs.curseapi.cursemeta.AddOn;
import com.therandomlabs.curseapi.cursemeta.CurseMeta;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFileList;
import com.therandomlabs.curseapi.file.ReleaseType;
import com.therandomlabs.curseapi.game.Game;
import com.therandomlabs.curseapi.util.Documents;
import com.therandomlabs.curseapi.util.URLs;
import com.therandomlabs.curseapi.util.Utils;
import com.therandomlabs.curseapi.widget.FileInfo;
import com.therandomlabs.curseapi.widget.MemberInfo;
import com.therandomlabs.curseapi.widget.ProjectInfo;
import com.therandomlabs.curseapi.widget.WidgetAPI;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.io.NetUtils;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.throwable.ThrowableHandling;
import com.therandomlabs.utils.wrapper.Wrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//TODO Images, Issues, Source, Pages, Wiki, get number of relations, get relations on specific pages
public final class CurseProject {
	public static final CurseProject NULL_PROJECT = new CurseProject();

	public static final String UNKNOWN_TITLE = "Unknown Name";
	public static final String CUSTOM_LICENSE = "Custom License";

	private static final Map<Integer, CurseProject> projects = new ConcurrentHashMap<>();

	//Some slugs redirect to other slugs, e.g. just-enough-items-jei -> jei
	private static final Map<Map.Entry<CurseForgeSite, String>, String> slugMappings =
			new ConcurrentHashMap<>();

	private final Map<RelationType, TRLList<Relation>> dependencies = new ConcurrentHashMap<>();
	private final Map<RelationType, TRLList<Relation>> dependents = new ConcurrentHashMap<>();
	private final boolean isNull;
	private final boolean curseMeta;
	private final Map<String, WeakReference<Document>> documentCache = new ConcurrentHashMap<>();
	//Incomplete list of files used as a cache
	private final CurseFileList incompleteFiles = new CurseFileList();
	private BufferedImage avatar;
	private URL url;
	private String urlString;
	private String slug;
	private URL mainCurseForgeURL;
	private String mainCurseForgeURLString;
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
	private CurseFileList files;
	private boolean forceMultithreadedFileSearches;

	private CurseProject() {
		isNull = true;
		curseMeta = false;
		site = CurseForgeSite.UNKNOWN;
		title = CurseProject.UNKNOWN_TITLE;
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
		url = project.getKey();
		document = project.getValue();
		reloadURL(CurseForge.toMainCurseForgeProject(document));

		isNull = false;
		curseMeta = false;

		reload(false);

		projects.put(id, this);
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object anotherObject) {
		if(anotherObject instanceof CurseProject) {
			return ((CurseProject) anotherObject).id == id;
		}

		return false;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[id=" + id + ",title=" + title + ",game=" + game + "]";
	}

	public boolean isNull() {
		return isNull;
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

	public String urlString() {
		return urlString;
	}

	public URL mainCurseForgeURL() {
		return mainCurseForgeURL;
	}

	public String mainCurseForgeURLString() {
		return mainCurseForgeURLString;
	}

	public boolean hasMainCurseForgePage() {
		return mainCurseForgeURL != null;
	}

	public String slug() {
		return slug;
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
				avatar = ImageIO.read(NetUtils.getInputStream(avatarURL));
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
		return ImageIO.read(NetUtils.getInputStream(thumbnailURL));
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
			licenseHTML = Documents.get(url + "/license");
			license = Documents.getPlainText(licenseHTML);

			if(license.endsWith("\n")) {
				license = StringUtils.removeLastChar(license);
			}
		}

		return licenseHTML;
	}

	public String shortDescription() {
		return shortDescription;
	}

	public String description() throws CurseException {
		descriptionHTML();
		return description;
	}

	public Element descriptionHTML() throws CurseException {
		if(descriptionHTML == null) {
			descriptionHTML = CurseMeta.getDescription(id);
			description = Documents.getPlainText(descriptionHTML);
		}

		return descriptionHTML;
	}

	public TRLList<Category> categories() {
		return categories;
	}

	public boolean forcedMultithreadedFileSearches() {
		return forceMultithreadedFileSearches;
	}

	public void forceMultithreadedFileSearches(boolean flag) {
		forceMultithreadedFileSearches = flag;
	}

	public CurseFile latestFile() throws CurseException {
		if(!incompleteFiles.isEmpty()) {
			return incompleteFiles.latest();
		}

		if(!shouldAvoidWidgetAPI() || CurseAPI.isCurseMetaEnabled()) {
			return filesDirect().latest();
		}

		final List<CurseFile> files = new TRLList<>();

		getFiles(Documents.get(url + "/files?page=1"), files);
		//Add to cache
		incompleteFiles.addAll(files);

		return files.get(0);
	}

	public CurseFile latestFile(Predicate<CurseFile> predicate) throws CurseException {
		if(!incompleteFiles.isEmpty()) {
			final CurseFile file = incompleteFiles.latest(predicate);
			if(file != null) {
				return file;
			}
		}

		if(shouldAvoidWidgetAPI() && !CurseAPI.isCurseMetaEnabled()) {
			final Wrapper<CurseFile> latestFile = new Wrapper<>();

			Documents.putTemporaryCache(this, documentCache);

			final List<CurseFile> files = Documents.iteratePages(
					this,
					url + "/files?",
					this::getFiles,
					file -> {
						if(predicate.test(file) && !latestFile.hasValue()) {
							latestFile.set(file);
							return false;
						}

						return true;
					},
					forceMultithreadedFileSearches
			);

			Documents.removeTemporaryCache(this);

			incompleteFiles.addAll(files);

			return latestFile.get();
		}

		return filesDirect().latest(predicate);
	}

	public CurseFileList files() throws CurseException {
		return filesDirect().clone();
	}

	public CurseFileList filesBetween(int oldID, int newID) throws CurseException {
		if(incompleteFiles.hasFileOlderThan(oldID) &&
				incompleteFiles.hasFileNewerThanOrEqualTo(newID)) {
			final CurseFileList files = incompleteFiles.clone();
			files.between(oldID, newID);
			return files;
		}

		if(shouldAvoidWidgetAPI() && !CurseAPI.isCurseMetaEnabled()) {
			Documents.putTemporaryCache(this, documentCache);

			final List<CurseFile> files = Documents.iteratePages(
					this,
					url + "/files?",
					this::getFiles,
					file -> file.id() >= oldID, //Continue as long as oldID has not been found
					forceMultithreadedFileSearches
			);

			Documents.removeTemporaryCache(this);

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

		if(!CurseAPI.isCurseMetaEnabled()) {
			try {
				final URL fileURL = URLs.of(url + "/files/" + id);
				return new CurseFile(this, id, fileURL, Documents.get(fileURL));
			} catch(CurseException ex) {
				if(!(ex.getCause() instanceof FileNotFoundException)) {
					throw ex;
				}
			}

			return CurseFile.nullFile(this.id, id);
		}

		return new CurseFile(this.id, game, CurseMeta.getFile(this.id, id));
	}

	public CurseFile fileClosestToID(int id, boolean preferOlder) throws CurseException {
		if(!CurseAPI.isCurseMetaEnabled()) {
			if(shouldAvoidWidgetAPI()) {
				Documents.putTemporaryCache(this, documentCache);

				incompleteFiles.addAll(Documents.iteratePages(
						this,
						url + "/files?",
						this::getFiles,
						file -> file.id() > id - 1,
						forceMultithreadedFileSearches
				));

				Documents.removeTemporaryCache(this);

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
		dependencies.put(relationType, getRelations("dependencies", relationType,
				onDependencyAdd));
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

	public void reloadURLs() throws CurseException {
		final Map.Entry<URL, Document> project = CurseForge.fromID(id);

		url = project.getKey();
		document = project.getValue();

		reloadURL(CurseForge.toMainCurseForgeProject(url));
	}

	public boolean reload() throws CurseException {
		return reload(false);
	}

	public void reloadFiles() throws CurseException {
		if(isNull()) {
			return;
		}

		if(!CurseAPI.isCurseMetaEnabled()) {
			if(shouldAvoidWidgetAPI()) {
				Documents.putTemporaryCache(this, documentCache);

				this.files = new CurseFileList(Documents.iteratePages(
						this,
						url + "/files?",
						this::getFiles,
						null,
						true
				));

				Documents.removeTemporaryCache(this);

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

		files = CurseFile.getFiles(id);
	}

	public void clearCache() {
		avatar = null;
		licenseHTML = null;
		license = null;
		dependencies.clear();
		dependents.clear();
	}

	private CurseFileList filesDirect() throws CurseException {
		if(files == null) {
			reloadFiles();
		}

		return files;
	}

	private TRLList<Relation> getRelations(String relationName, RelationType relationType,
			Predicate<Relation> onRelationAdd) throws CurseException {
		String baseURL = urlString() + "/relations/" + relationName;

		if(relationType == RelationType.ALL_TYPES) {
			baseURL += "?";
		} else {
			baseURL += "?filter-related-" + relationName + "=" + relationType.ordinal() + "&";
		}

		Documents.putTemporaryCache(this, documentCache);

		final TRLList<Relation> relationList = Documents.iteratePages(
				this,
				baseURL,
				(document, relations) -> documentToRelations(document, relations, relationType),
				onRelationAdd,
				true
		);

		Documents.removeTemporaryCache(this);

		return relationList;
	}

	private void documentToRelations(Element document, List<Relation> relations,
			RelationType relationType) throws CurseException {
		for(Element relation : document.getElementsByClass("project-list-item")) {
			final String projectURL =
					Documents.getValue(relation, "class=name-wrapper;tag=a;absUrl=href");

			//Some elements are empty for some reason
			if(!projectURL.isEmpty()) {
				relations.add(getRelationInfo(relation, URLs.of(projectURL), relationType));
			}
		}
	}

	private Relation getRelationInfo(Element element, URL url, RelationType relationType)
			throws CurseException {
		final String title = Documents.getValue(element, "class=name-wrapper;tag=a;text");

		final String author = Documents.getValue(element, "tag=span;tag=a;text");

		final int downloads = Integer.parseInt(Documents.getValue(
				element,
				"class=e-download-count;text"
		).replaceAll(",", ""));

		final long lastUpdateTime = Long.parseLong(Documents.getValue(
				element, "class=standard-date;attr=data-epoch"
		));

		final String shortDescription = Documents.getValue(element, "class=description;tag=p;text");

		final Category[] categories = getCategories(
				element.getElementsByClass("category-icons")
		).toArray(new Category[0]);

		return new Relation(
				url, title, author, downloads, lastUpdateTime, shortDescription, categories, this,
				relationType
		);
	}

	private void reloadURL(URL mainCurseForgeURL) {
		urlString = url.toString();
		slug = ArrayUtils.last(url.getPath().split("/"));

		this.mainCurseForgeURL = mainCurseForgeURL;
		mainCurseForgeURLString = mainCurseForgeURL == null ? null : mainCurseForgeURL.toString();
	}

	private boolean reload(boolean useWidgetAPI) throws CurseException {
		if(isNull()) {
			return true;
		}

		if(curseMeta) {
			reloadCurseMeta();
		}

		if(document == null) {
			try {
				final Map.Entry<URL, Document> project = CurseForge.fromID(id);

				url = project.getKey();
				document = project.getValue();

				reloadURL(CurseForge.toMainCurseForgeProject(url));
			} catch(InvalidProjectIDException ex) {
				ThrowableHandling.handleWithoutExit(ex);
				return false;
			}
		}

		site = CurseForgeSite.fromURL(url);

		if(shouldAvoidWidgetAPI() || !useWidgetAPI || mainCurseForgeURL == null) {
			id = CurseForge.getID(document);

			title = Documents.getValue(document, "class=project-title;class=overflow-tip;text");

			shortDescription = Documents.getValue(document, "name=description=1;attr=content");

			game = site.game();

			type = ProjectType.get(site, ArrayUtils.last(Documents.getValue(
					document,
					"tag=title;text"
			).split(" - ")));

			try {
				thumbnailURLString = Documents.getValue(
						document,
						"class=e-avatar64;tag=img;absUrl=src"
				);

				thumbnailURL = URLs.of(thumbnailURLString);
			} catch(CurseException ex) {
				thumbnailURLString = CurseAPI.PLACEHOLDER_THUMBNAIL_URL_STRING;
				thumbnailURL = CurseAPI.PLACEHOLDER_THUMBNAIL_URL;
			}

			members.clear();

			for(Element member : document.getElementsByClass("project-members")) {
				members.add(new Member(
						MemberType.fromName(Documents.getValue(member, "class=title;text")),
						Documents.getValue(member, "tag=span;text")
				));
			}

			downloads = Integer.parseInt(Documents.getValue(
					document,
					"class=info-data=3;text"
			).replaceAll(",", ""));

			creationTime = Utils.parseTime(Documents.getValue(
					document,
					"class=project-details;class=standard-date;attr=data-epoch"
			));

			try {
				donateURLString = Documents.getValue(
						document,
						"class=icon-donate;attr=href;absUrl=href"
				);
			} catch(CurseException ignored) {}

			donateURL = donateURLString == null ? null : URLs.of(donateURLString);

			licenseName = Documents.getValue(document, "class=info-data=4;tag=a;text");
		} else {
			ProjectInfo info;

			try {
				info = WidgetAPI.get(mainCurseForgeURL.getPath());
			} catch(CurseException ex) {
				ThrowableHandling.handleWithoutExit(ex);
				return reload(false);
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
			creationTime = Utils.parseTime(info.created_at);
			donateURL = info.donate;
			donateURLString = donateURL == null ? null : donateURLString;
			licenseName = info.license;
			widgetInfoFiles = info.versions;
		}

		descriptionHTML = Documents.get(document, "class=project-description");

		description = Documents.getPlainText(descriptionHTML);

		categories = getCategories(
				document.getElementsByClass("project-categories").get(0).getElementsByTag("li")
		).toImmutableList();

		avatarURLString = Documents.getValue(document, "class=e-avatar64;absUrl=href");

		avatarURL = avatarURLString.isEmpty() ?
				CurseAPI.PLACEHOLDER_THUMBNAIL_URL : URLs.of(avatarURLString);

		//So it can be garbage collected
		document = null;

		return true;
	}

	private void reloadCurseMeta() throws CurseException {
		final AddOn addon = CurseMeta.getAddOn(id);

		id = addon.Id;
		title = addon.Name;
		game = Game.fromID(addon.GameId);
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

		reloadURL(mainCurseForgeURL);
	}

	private void getFiles(Element document, List<CurseFile> files) throws CurseException {
		try {
			actuallyGetFiles(document, files);
		} catch(NullPointerException | NumberFormatException ex) {
			throw CurseException.fromThrowable(ex);
		}
	}

	private void actuallyGetFiles(Element document, List<CurseFile> files) throws CurseException {
		for(Element file : document.getElementsByClass("project-file-list-item")) {
			final int id = Integer.parseInt(ArrayUtils.last(Documents.getValue(
					file,
					"class=twitch-link;attr=href"
			).split("/")));

			final URL url = URLs.of(Documents.getValue(
					file,
					"class=twitch-link;attr=href;absUrl=href"
			));

			final String name = Documents.getValue(file, "class=twitch-link;text");

			//<div class="alpha-phase tip">
			final ReleaseType type = ReleaseType.fromName(Documents.getValue(
					file,
					"class=project-file-release-type;class=tip;attr=title"
			));

			final String[] versions;

			if(file.getElementsByClass("additional-versions").isEmpty()) {
				final String version = Documents.getValue(file, "class=version-label;text");

				if(version.equals("-")) {
					versions = new String[0];
				} else {
					versions = new String[] {
							version
					};
				}
			} else {
				String value = Documents.getValue(file, "class=additional-versions;attr=title");

				value = value.substring(5, value.length() - 6);

				versions = value.split("</div><div>");
			}

			final String fileSize = Documents.getValue(file, "class=project-file-size;text");

			final int downloads = Integer.parseInt(Documents.getValue(
					file,
					"class=project-file-downloads;text"
			).replaceAll(",", ""));

			final String uploadedAt =
					Documents.getValue(file, "class=standard-date;attr=data-epoch");

			files.add(new CurseFile(CurseProject.this, new FileInfo(
					id, url, name, type, versions, fileSize, downloads, uploadedAt
			)));
		}
	}

	private boolean shouldAvoidWidgetAPI() {
		return !CurseAPI.isWidgetAPIEnabled() || mainCurseForgeURL == null;
	}

	public static CurseProject fromID(String id) throws CurseException {
		return fromID(id, false);
	}

	public static CurseProject fromID(String id, boolean dontThrowIfInvalidID)
			throws CurseException {
		return fromID(Integer.parseInt(id), dontThrowIfInvalidID);
	}

	public static CurseProject fromID(int id) throws CurseException {
		return fromID(id, false);
	}

	public static CurseProject fromID(int id, boolean ignoreInvalidID) throws CurseException {
		CurseProject project = projects.get(id);

		if(project != null) {
			return project;
		}

		try {
			return new CurseProject(id);
		} catch(InvalidProjectIDException ex) {
			if(!ignoreInvalidID) {
				throw ex;
			}
		}

		return nullProject(id);
	}

	public static CurseProject fromSlug(String site, String slug) throws CurseException {
		return fromSlug(site, slug, true);
	}

	public static CurseProject fromSlug(CurseForgeSite site, String slug)
			throws CurseException {
		return fromSlug(site, slug, true);
	}

	public static CurseProject fromSlug(String site, String slug, boolean followRedirections)
			throws CurseException {
		return fromSlug(CurseForgeSite.fromString(site), slug, followRedirections);
	}

	public static CurseProject fromSlug(CurseForgeSite site, String slug,
			boolean followRedirections) throws CurseException {
		final Map.Entry<CurseForgeSite, String> slugKey = new AbstractMap.SimpleEntry<>(site, slug);
		final String slugMapping = slugMappings.get(slugKey);
		final String toFind = slugMapping == null ? slug : slugMapping;

		for(CurseProject project : projects.values()) {
			if(project.site == site && toFind.equals(project.slug)) {
				return project;
			}
		}

		final CurseProject project = fromURL(site.url() + "projects/" + slug, followRedirections);

		if(!slug.equals(project.slug)) {
			slugMappings.put(slugKey, project.slug);
		}

		return project;
	}

	public static CurseProject fromURL(String url) throws CurseException {
		return fromURL(url, false);
	}

	public static CurseProject fromURL(URL url) throws CurseException {
		return fromURL(url, false);
	}

	public static CurseProject fromURL(String url, boolean followRedirections)
			throws CurseException {
		return fromURL(URLs.of(url), followRedirections);
	}

	public static CurseProject fromURL(URL url, boolean followRedirections) throws CurseException {
		if(followRedirections) {
			url = URLs.redirect(url);
		}

		final String urlString = url.toString();

		for(CurseProject project : projects.values()) {
			if(urlString.equals(project.urlString)) {
				return project;
			}
		}

		return new CurseProject(new AbstractMap.SimpleEntry<>(
				url,
				InvalidCurseForgeProjectException.validate(url)
		));
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
		slugMappings.clear();
	}

	public static boolean isCached(int id) {
		return projects.containsKey(id);
	}

	private static TRLList<Category> getCategories(Elements categoryElements)
			throws CurseException {
		final TRLList<Category> categories = new TRLList<>();

		for(Element category : categoryElements) {
			final String name = Documents.getValue(category, "tag=a;attr=title");

			final URL url = URLs.of(Documents.getValue(category, "tag=a;absUrl=href"));

			final URL thumbnailURL = URLs.of(Documents.getValue(category, "tag=img;absUrl=src"));

			categories.add(new Category(name, url, thumbnailURL));
		}

		return categories;
	}
}
