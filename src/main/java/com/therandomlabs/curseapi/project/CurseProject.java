package com.therandomlabs.curseapi.project;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.therandomlabs.curseapi.cursemeta.CurseMeta;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFileList;
import com.therandomlabs.curseapi.game.Game;
import com.therandomlabs.curseapi.util.Documents;
import com.therandomlabs.curseapi.util.FileListParser;
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

	private final Map<String, Documents.DocumentCache> documentCache = new ConcurrentHashMap<>();

	//Incomplete list of files used as a cache
	private final CurseFileList incompleteFiles = new CurseFileList();

	private URL url;
	private String urlString;

	private String slug;

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
	private BufferedImage avatar;

	private URL thumbnailURL;
	private String thumbnailURLString;

	private TRLList<Member> members = new TRLList<>();

	private int downloads;

	private ZonedDateTime creationTime;

	private String licenseName;
	private Element licenseHTML;
	private String license;

	private URL donationURL;
	private String donationURLString;

	private Map<String, FileInfo[]> widgetInfoFiles;

	private CurseFileList files;

	private boolean forceMultithreadedFileSearches;

	private CurseProject() {
		isNull = true;
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
		reloadURL();

		isNull = false;

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

	public URL donationURL() {
		return donationURL;
	}

	public String donationURLString() {
		return donationURLString;
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
			licenseHTML = Documents.get(CurseForge.URL + "project/" + id + "/license");
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

		if(CurseAPI.isWidgetAPIEnabled() || CurseAPI.isCurseMetaEnabled()) {
			return filesDirect().latest();
		}

		final List<CurseFile> files = new TRLList<>();

		getFiles(Documents.get(url + "/files/all?page=1"), files);
		//Add to cache
		incompleteFiles.addAll(files);

		return files.isEmpty() ? null : files.get(0);
	}

	public CurseFile latestFile(Predicate<CurseFile> predicate) throws CurseException {
		if(!incompleteFiles.isEmpty()) {
			final CurseFile file = incompleteFiles.latest(predicate);
			if(file != null) {
				return file;
			}
		}

		if(!CurseAPI.isWidgetAPIEnabled() && !CurseAPI.isCurseMetaEnabled()) {
			final Wrapper<CurseFile> latestFile = new Wrapper<>();

			Documents.putTemporaryCache(this, documentCache);

			final List<CurseFile> files = Documents.iteratePages(
					this,
					url + "/files/all?",
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

		if(!CurseAPI.isWidgetAPIEnabled() && !CurseAPI.isCurseMetaEnabled()) {
			Documents.putTemporaryCache(this, documentCache);

			final List<CurseFile> files = Documents.iteratePages(
					this,
					url + "/files/all?",
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
			if(!CurseAPI.isWidgetAPIEnabled()) {
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

		reloadURL();
	}

	public boolean reload() throws CurseException {
		return reload(false);
	}

	public void reloadFiles() throws CurseException {
		if(isNull()) {
			return;
		}

		if(CurseAPI.isCurseMetaEnabled()) {
			files = CurseFile.getFiles(id);
			return;
		}

		if(CurseAPI.isWidgetAPIEnabled()) {
			final TRLList<CurseFile> files = new TRLList<>(widgetInfoFiles.size() * 10);

			for(Map.Entry<String, FileInfo[]> entry : widgetInfoFiles.entrySet()) {
				for(FileInfo info : entry.getValue()) {
					files.add(new CurseFile(this, info));
				}
			}

			this.files = new CurseFileList(files);
			return;
		}

		Documents.putTemporaryCache(this, documentCache);

		this.files = new CurseFileList(Documents.iteratePages(
				this,
				url + "/files?",
				this::getFiles,
				null,
				true
		));

		Documents.removeTemporaryCache(this);
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
		for(Element relation : document.getElementsByClass("project-listing-row")) {
			final String projectURL = Documents.getValue(relation, "tag=a;absUrl=href");

			//Some elements are empty for some reason
			if(!projectURL.isEmpty()) {
				relations.add(getRelationInfo(relation, URLs.of(projectURL), relationType));
			}
		}
	}

	private Relation getRelationInfo(Element element, URL url, RelationType relationType)
			throws CurseException {
		final String title = Documents.getValue(element, "tag=h3;text");

		final String author = Documents.getValue(element, "class=font-bold=1;text");

		final int downloads = 0;/*Integer.parseInt(Documents.getValue(
				element,
				"class=text-xs;text"
		).replaceAll(",", ""));*/

		final long lastUpdateTime = Long.parseLong(Documents.getValue(
				element, "class=standard-datetime;attr=data-epoch"
		));

		final String shortDescription = Documents.getValue(element, "class=text-sm;text");

		final Category[] categories = getCategories(
				element.getElementsByClass("-mx-1")
		).toArray(new Category[0]);

		return new Relation(
				url, title, author, downloads, lastUpdateTime, shortDescription, categories, this,
				relationType
		);
	}

	private void reloadURL() {
		urlString = url.toString();
		slug = ArrayUtils.last(url.getPath().split("/"));
	}

	private boolean reload(boolean useWidgetAPI) throws CurseException {
		if(isNull()) {
			return true;
		}

		/*if(CurseAPI.isCurseMetaEnabled()) {
			reloadCurseMeta();
		}*/

		if(document == null) {
			try {
				final Map.Entry<URL, Document> project = CurseForge.fromID(id);

				url = project.getKey();
				document = project.getValue();

				reloadURL();
			} catch(InvalidProjectIDException ex) {
				ThrowableHandling.handleWithoutExit(ex);
				return false;
			}
		}

		site = CurseForgeSite.MINECRAFT;//CurseForgeSite.fromURL(url);

		if(!CurseAPI.isWidgetAPIEnabled() || !useWidgetAPI) {
			id = CurseForge.getID(document);

			title = Documents.getValue(document, "tag=meta=5;attr=content");

			shortDescription = Documents.getValue(document, "name=description=1;attr=content");

			game = site.game();

			type = ProjectType.get(site, ArrayUtils.fromLast(Documents.getValue(
					document,
					"tag=title;text"
			).split(" - "), 2));

			try {
				thumbnailURLString = Documents.getValue(document, "tag=meta=8;attr=content");

				thumbnailURL = URLs.of(thumbnailURLString);
			} catch(CurseException ex) {
				thumbnailURLString = CurseAPI.PLACEHOLDER_THUMBNAIL_URL_STRING;
				thumbnailURL = CurseAPI.PLACEHOLDER_THUMBNAIL_URL;
			}

			members.clear();

			for(Element member :
					document.getElementsByClass("pb-4").get(2).getElementsByClass("mb-2")) {
				members.add(new Member(
						MemberType.fromName(Documents.getValue(member, "class=text-xs;text")),
						Documents.getValue(member, "tag=span;text")
				));
			}

			downloads = Integer.parseInt(StringUtils.removeLastChars(Documents.getValue(
					document,
					"class=mr-2=2;text"
			).replaceAll(",", ""), 10));

			creationTime = Utils.parseTime(Documents.getValue(
					document,
					"class=standard-datetime=-0;attr=data-epoch"
			));

			try {
				donationURLString = Documents.getValue(
						document,
						"class=w-15;attr=href;absUrl=href"
				);
			} catch(CurseException ignored) {}

			donationURL = donationURLString == null ? null : URLs.of(donationURLString);

			licenseName = Documents.getValue(document, "class=mb-3=1;tag=a;text");
		} else {
			ProjectInfo info;

			try {
				info = WidgetAPI.get(url.getPath());
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
			donationURL = info.donate;
			donationURLString = donationURL == null ? null : donationURLString;
			licenseName = info.license;
			widgetInfoFiles = info.versions;
		}

		//TODO replace linkouts
		descriptionHTML = Documents.get(document, "class=project-detail__content");

		description = Documents.getPlainText(descriptionHTML);

		categories = getCategories(
				document.getElementsByClass("mt-3").get(0).getElementsByTag("a")
		).toImmutableList();

		avatarURLString = Documents.getValue(document, "class=bg-white;absUrl=data-featherlight");

		avatarURL = avatarURLString.isEmpty() ?
				CurseAPI.PLACEHOLDER_THUMBNAIL_URL : URLs.of(avatarURLString);

		//So it can be garbage collected
		document = null;

		return true;
	}

	/*private void reloadCurseMeta() throws CurseException {
		final CMAddon addon = CurseMeta.getAddon(id);

		id = addon.id;
		title = addon.name;
		game = Game.fromID(addon.gameId);
		mainCurseForgeURL = addon.websiteUrl;
		avatarURL = addon.avatarUrl == null ? CurseAPI.PLACEHOLDER_THUMBNAIL_URL : addon.avatarUrl;
		avatarURLString = avatarURL.toString();
		thumbnailURL = avatarURL;
		thumbnailURLString = avatarURLString;
		members = new TRLList<>(Member.fromAuthors(addon.authors));
		downloads = addon.downloadCount;
		creationTime = null;
		//donation URL seems to always be null
		//donationURL = addon.donationUrl;
		//donationURLString = donationURL == null ? null : donationURL.toString();
		shortDescription = addon.summary;
		categories = new TRLList<>(Category.fromAddOnCategories(addon.categories));

		reloadURL(mainCurseForgeURL);
	}*/

	private void getFiles(Element document, List<CurseFile> files) throws CurseException {
		FileListParser.getFiles(this, document, files);
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
			final String name = Documents.getValue(category, "tag=figure;attr=title");

			final URL url = URLs.of(Documents.getValue(category, "tag=a;absUrl=href"));

			final URL thumbnailURL = URLs.of(Documents.getValue(category, "tag=img;absUrl=src"));

			categories.add(new Category(name, url, thumbnailURL));
		}

		return categories;
	}
}
