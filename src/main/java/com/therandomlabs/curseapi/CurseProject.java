package com.therandomlabs.curseapi;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import org.jsoup.nodes.Element;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.curseapi.curseforge.CurseForgeSite;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.curseapi.widget.FileInfo;
import com.therandomlabs.curseapi.widget.MemberInfo;
import com.therandomlabs.curseapi.widget.ProjectInfo;
import com.therandomlabs.curseapi.widget.WidgetAPI;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.misc.StopSwitch;
import com.therandomlabs.utils.network.NetworkUtils;
import com.therandomlabs.utils.runnable.RunnableWithInput;

//TODO Images, Issues, Source, Pages, Wiki
public class CurseProject {
	private static final TRLList<CurseProject> projects = new TRLList<>(100);

	private URL url;
	private URL newCurseForgeURL;
	private ProjectInfo widgetInfo;
	private BufferedImage thumbnail;
	private CurseFileList files;

	private final Map<RelationType, List<URL>> dependencies = new HashMap<>();
	private final Map<RelationType, List<URL>> dependents = new HashMap<>();

	private CurseProject(int id) throws CurseException {
		this(CurseForge.fromID(id));
	}

	private CurseProject(URL url) throws CurseException {
		CurseException.validateProject(url);

		this.url = url;
		this.newCurseForgeURL = CurseForge.toNewCurseForgeProject(url);
		reloadWidgetInfo();
		projects.add(this);
	}

	public int id() {
		return widgetInfo.id;
	}

	public URL url() {
		return url;
	}

	public String urlString() {
		return url.toString();
	}

	public CurseForgeSite site() {
		return CurseForgeSite.valueOf(url);
	}

	public String title() {
		return widgetInfo.title;
	}

	public Game game() {
		return widgetInfo.game;
	}

	public String type() {
		return widgetInfo.type;
	}

	public URL thumbnailURL() {
		return widgetInfo.thumbnail;
	}

	public String thumbnailURLString() {
		return widgetInfo.thumbnail.toString();
	}

	public BufferedImage thumbnail(boolean reload) throws IOException {
		if(thumbnail == null || reload) {
			thumbnail = ImageIO.read(NetworkUtils.download(thumbnailURL()));
		}

		return thumbnail;
	}

	public List<MemberInfo> members() throws CurseException {
		try {
			return new ImmutableList<>(ArrayUtils.clone(widgetInfo.members.clone()));
		} catch(Exception ex) {
			throw new CurseException(ex);
		}
	}

	public int monthlyDownloads() {
		return widgetInfo.downloads.monthly;
	}

	public int totalDownloads() {
		return widgetInfo.downloads.total;
	}

	public ZonedDateTime lastUpdateTime() {
		return ZonedDateTime.parse(widgetInfo.files[0].uploaded_at);
	}

	public ZonedDateTime creationTime() {
		return ZonedDateTime.parse(widgetInfo.created_at);
	}

	public ReleaseType releaseType() {
		return widgetInfo.files[0].type;
	}

	public URL donateURL() {
		return widgetInfo.donate;
	}

	public String donateURLString() {
		return widgetInfo.donate.toString();
	}

	public String license() {
		return widgetInfo.license;
	}

	public String licenseHTML() throws CurseException {
		return DocumentUtils.get(urlString() + "/license").html();
	}

	public String description() {
		return widgetInfo.description;
	}

	public String descriptionHTML() throws CurseException {
		return DocumentUtils.getValue(url, "class=project-description;html");
	}

	public List<String> categories() {
		return new ImmutableList<>(widgetInfo.categories);
	}

	public CurseFileList files() throws CurseException {
		if(files == null) {
			reloadFiles();
		}

		return files;
	}

	public CurseFile fileFromID(int id) throws CurseException {
		for(CurseFile file : files()) {
			if(file.id() == id) {
				return file;
			}
		}
		return null;
	}

	public CurseFile recommendedDownload() throws CurseException {
		return fileFromID(widgetInfo.download.id);
	}

	public List<URL> dependencies() throws CurseException {
		return dependencies(RelationType.ALL_TYPES);
	}

	public List<URL> dependencies(RelationType relationType) throws CurseException {
		if(!dependencies.containsKey(relationType)) {
			reloadDependencies(relationType);
		}

		return dependencies.get(relationType);
	}

	public void reloadDependencies(RelationType relationType) throws CurseException {
		reloadDependencies(relationType, null, null);
	}

	public void reloadDependencies(RelationType relationType,
			RunnableWithInput<URL> onDependencyAdd, StopSwitch stopSwitch) throws CurseException {
		dependencies.put(relationType,
				getRelations("dependencies", relationType, onDependencyAdd, stopSwitch));
	}

	public List<URL> dependents() throws CurseException {
		return dependents(RelationType.ALL_TYPES);
	}

	public List<URL> dependents(RelationType relationType) throws CurseException {
		if(!dependents.containsKey(relationType)) {
			reloadDependents(relationType);
		}

		return dependents.get(relationType);
	}

	public void reloadDependents(RelationType relationType) throws CurseException {
		reloadDependents(relationType, null, null);
	}

	public void reloadDependents(RelationType relationType,
			RunnableWithInput<URL> onDependentAdd, StopSwitch stopSwitch) throws CurseException {
		dependents.put(relationType,
				getRelations("dependents", relationType, onDependentAdd, stopSwitch));
	}

	public void reloadURL() throws CurseException {
		url = CurseForge.fromID(id());
		newCurseForgeURL = CurseForge.toNewCurseForgeProject(url);
	}

	public void reloadWidgetInfo() throws CurseException {
		widgetInfo = WidgetAPI.get(newCurseForgeURL.getPath());
		thumbnail = null;
	}

	public ProjectInfo widgetInfo() {
		return widgetInfo.clone();
	}

	public void reloadFiles() throws CurseException {
		final List<CurseFile> files = new ArrayList<>(widgetInfo.versions.size());
		for(Map.Entry<String, FileInfo[]> entry : widgetInfo.versions.entrySet()) {
			for(FileInfo info : entry.getValue()) {
				files.add(new CurseFile(this, info));
			}
		}

		//Should already be sorted by newest
		this.files = CurseFileList.ofUnsorted(files);
		this.files.sortType = CurseFileList.SortType.NEWEST;
	}

	public void clearRelationCache() {
		clearDependencyCache();
		clearDependentCache();
	}

	public void clearDependencyCache() {
		dependencies.clear();
	}

	public void clearDependencyCache(RelationType relationType) {
		dependencies.remove(relationType);
	}

	public void clearDependentCache() {
		dependents.clear();
	}

	public void clearDependentCache(RelationType relationType) {
		dependents.remove(relationType);
	}

	@Override
	public String toString() {
		return getClass().getName() +
				"[id=" + id() + ",title=" + title() + ",game=" + game() + "]";
	}

	@Override
	public int hashCode() {
		return id();
	}

	@Override
	public boolean equals(Object anotherObject) {
		if(anotherObject instanceof CurseProject) {
			return ((CurseProject) anotherObject).hashCode() == hashCode();
		}

		return false;
	}

	private List<URL> getRelations(String relationName, RelationType relationType,
			RunnableWithInput<URL> onRelationAdd, StopSwitch stopSwitch)
			throws CurseException {
		String baseURL = urlString() + "/relations/" + relationName;

		if(relationType == RelationType.ALL_TYPES) {
			baseURL += "?";
		} else {
			baseURL += "?filter-related-" + relationName + "=" + relationType.ordinal() + "&";
		}

		return DocumentUtils.iteratePages(baseURL, this::documentToRelations, onRelationAdd,
				stopSwitch);
	}

	private void documentToRelations(Element document, List<URL> relations)
			throws CurseException {
		for(Element relation : document.getElementsByClass("project-list-item")) {
			final String projectURL =
					DocumentUtils.getValue(relation, "class=name-wrapper;attr=href;absUrl=href");
			//Some elements are empty
			if(!projectURL.isEmpty()) {
				relations.add(URLUtils.url(projectURL));
			}
		}
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

	public static CurseProject fromURL(String url) throws CurseException {
		return fromURL(url, false);
	}

	public static CurseProject fromURL(String url, boolean followRedirections)
			throws CurseException {
		return fromURL(URLUtils.url(url), false);
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

	public static CurseProject fromPath(String site, String path) throws CurseException {
		return fromPath(CurseForgeSite.valueOf(site.toUpperCase(Locale.ENGLISH)), path);
	}

	public static CurseProject fromPath(CurseForgeSite site, String path) throws CurseException {
		return fromURL(site.getURL() + "projects/" + path);
	}

	public static void clearProjectCache() {
		projects.clear();
	}
}
