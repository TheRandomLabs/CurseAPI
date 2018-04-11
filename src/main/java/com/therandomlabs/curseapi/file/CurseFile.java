package com.therandomlabs.curseapi.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Objects;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.Game;
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
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.io.NIOUtils;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.network.NetworkUtils;
import org.jsoup.nodes.Element;

//TODO Additional Files
public final class CurseFile {
	private final CurseProject project;
	private final FileStatus status;
	private final URL url;
	private final int id;
	private final String name;
	private String nameOnDisk;
	private URL downloadURL;
	private final ReleaseType releaseType;
	private final ZonedDateTime uploadTime;
	private String fileSize;
	private final int downloads;
	private final TRLList<CurseProject> dependencies;
	private final TRLList<String> gameVersions;
	private final TRLList<MinecraftVersion> minecraftVersions;
	private final boolean curseMeta;

	public CurseFile(CurseProject project, AddOnFile info) throws CurseException {
		this(project, info.FileStatus, info.Id, info.FileName, info.FileNameOnDisk,
				info.DownloadURL, info.ReleaseType, info.FileDate, null, -1,
				AddOnFileDependency.toProjects(info.Dependencies), info.GameVersion, true);
	}

	public CurseFile(CurseProject project, FileInfo info) throws CurseException {
		this(project, FileStatus.NORMAL, info.id, info.name, null, null, info.type,
				info.uploaded_at, info.filesize, info.downloads, null, info.versions, false);
	}

	public CurseFile(CurseProject project, FileStatus status, int id, String name,
			String nameOnDisk, URL downloadURL, ReleaseType releaseType, String uploadTime,
			String fileSize, int downloads, TRLList<CurseProject> dependencies,
			String[] gameVersions, boolean curseMeta) throws CurseException {
		this.project = project;
		this.status = status;

		url = URLUtils.url(project.urlString() + "/files/" + id);

		this.id = id;
		this.name = name;
		this.nameOnDisk = nameOnDisk;
		this.downloadURL = downloadURL;
		this.releaseType = releaseType;
		this.uploadTime = MiscUtils.parseTime(uploadTime);
		this.fileSize = fileSize;
		this.downloads = downloads;
		this.dependencies =
				dependencies == null ? getDependencies(url) : dependencies.toImmutableList();

		final TRLList<String> gameVersionList = new TRLList<>();
		for(String gameVersion : gameVersions) {
			if(!gameVersion.startsWith("Java ")) {
				gameVersionList.add(gameVersion);
			}
		}
		this.gameVersions = gameVersionList.toImmutableList();

		if(project.game() == Game.MINECRAFT) {
			final TRLList<MinecraftVersion> minecraftVersions =
					CollectionUtils.convert(this.gameVersions, MinecraftVersion::fromString);
			minecraftVersions.removeIf(Objects::isNull);
			minecraftVersions.sort();
			this.minecraftVersions = minecraftVersions.toImmutableList();
		} else {
			minecraftVersions = null;
		}

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

	public String nameOnDisk() throws CurseException {
		if(nameOnDisk == null) {
			nameOnDisk = DocumentUtils.getValue(url, "class=details-info;class=info-data;text");
		}

		return nameOnDisk;
	}

	public URL url() {
		return url;
	}

	public String urlString() {
		return url.toString();
	}

	public URL downloadURL() throws CurseException {
		if(downloadURL == null) {
			downloadURL = CurseForge.getFileURL(project.id(), id);
		}

		return downloadURL;
	}

	public String downloadURLString() throws CurseException {
		return downloadURL().toString();
	}

	public ReleaseType releaseType() {
		return releaseType;
	}

	public TRLList<CurseProject> dependencies() {
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

	public String fileSize() throws CurseException {
		if(fileSize == null) {
			fileSize = DocumentUtils.getValue(url, "class=details-info;class=info-data=3;text");
		}

		return fileSize;
	}

	public int downloads() {
		return downloads;
	}

	public String md5() throws CurseException {
		return DocumentUtils.getValue(url, "class=md5;text");
	}

	public boolean hasChangelog() throws CurseException {
		String changelog = changelog().trim().toLowerCase(Locale.ENGLISH);
		if(StringUtils.lastChar(changelog) == '.') {
			changelog = StringUtils.removeLastChar(changelog);
		}
		return !changelog.equals("no changelog provided") && !changelog.equals("n/a");
	}

	public String changelog() throws CurseException {
		return DocumentUtils.getPlainText(changelogHTML());
	}

	public Element changelogHTML() throws CurseException {
		if(curseMeta) {
			return CurseMeta.getChangelog(project.id(), id);
		}

		return DocumentUtils.get(url, "class=logbox");
	}

	public String uploader() throws CurseException {
		return DocumentUtils.getValue(url, "class=user-tag;tag=a=1;text");
	}

	public URL uploaderURL() throws CurseException {
		return URLUtils.url(DocumentUtils.getValue(url, "class=user-tag;tag=a=1;absUrl=href"));
	}

	public CurseProject project() {
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

	@SuppressWarnings("all")
	private static TRLList<CurseProject> getDependencies(URL url) throws CurseException {
		//TODO parse from HTML
		return null;
	}
}
