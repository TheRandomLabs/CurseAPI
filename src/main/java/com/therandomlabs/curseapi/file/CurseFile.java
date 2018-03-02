package com.therandomlabs.curseapi.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import org.jsoup.nodes.Element;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.Game;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.MiscUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.curseapi.widget.FileInfo;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.io.NIOUtils;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.network.NetworkUtils;

//TODO Additional Files
public class CurseFile {
	private final CurseProject project;
	private final FileInfo widgetInfo;

	private final TRLList<String> gameVersions;
	private final MinecraftVersion minecraftVersion;
	private final List<MinecraftVersion> minecraftVersions;

	private final URL url;

	protected CurseFile(CurseProject project, FileInfo widgetInfo) throws CurseException {
		this.project = project;
		this.widgetInfo = widgetInfo;

		gameVersions = new ImmutableList<>(widgetInfo.versions);

		if(project.game() == Game.MINECRAFT) {
			final TRLList<MinecraftVersion> minecraftVersions =
					CollectionUtils.convert(gameVersions, MinecraftVersion::fromString);
			minecraftVersions.removeIf(version -> version == null);
			minecraftVersions.sort();
			this.minecraftVersions = minecraftVersions.toImmutableList();
			minecraftVersion = minecraftVersions.get(0);
		} else {
			minecraftVersion = null;
			minecraftVersions = null;
		}

		url = URLUtils.url(project.urlString() + "/files/" + widgetInfo.id);
	}

	protected CurseFile(CurseFile file) {
		this.project = file.project;
		this.widgetInfo = file.widgetInfo;
		this.gameVersions = file.gameVersions;
		this.minecraftVersion = file.minecraftVersion;
		this.minecraftVersions = file.minecraftVersions;
		this.url = file.url;
	}

	public int id() {
		return widgetInfo.id;
	}

	public String fileName() throws CurseException {
		return NetworkUtils.getFileName(fileURL());
	}

	public URL url() {
		return url;
	}

	public String urlString() {
		return url.toString();
	}

	public URL fileURL() throws CurseException {
		return CurseForge.getFileURL(project.id(), widgetInfo.id);
	}

	public String fileURLString() throws CurseException {
		return fileURL().toString();
	}

	public String name() {
		return widgetInfo.name;
	}

	public ReleaseType releaseType() {
		return widgetInfo.type;
	}

	public String gameVersion() {
		return widgetInfo.version;
	}

	public TRLList<String> gameVersions() {
		return gameVersions;
	}

	public MinecraftVersion minecraftVersion() {
		return minecraftVersion;
	}

	public List<MinecraftVersion> minecraftVersions() {
		return minecraftVersions;
	}

	public ZonedDateTime uploadTime() {
		return MiscUtils.parseTime(widgetInfo.uploaded_at);
	}

	public String fileSize() {
		return widgetInfo.filesize;
	}

	public int downloads() {
		return widgetInfo.downloads;
	}

	public String md5() throws CurseException {
		return DocumentUtils.getValue(url, "class=md5;text");
	}

	public Element changelogHTML() throws CurseException {
		return DocumentUtils.get(url, "class=logbox");
	}

	public String changelog() throws CurseException {
		return DocumentUtils.getPlainText(changelogHTML());
	}

	public boolean changelogProvided() throws CurseException {
		String changelog = changelog().trim().toLowerCase(Locale.ENGLISH);
		if(StringUtils.lastChar(changelog) == '.') {
			changelog = StringUtils.removeLastChar(changelog);
		}
		return changelog.equals("no changelog provided") || changelog.equals("n/a");
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

	public FileInfo widgetInfo() {
		return widgetInfo.clone();
	}

	public InputStream download() throws CurseException, IOException {
		return NetworkUtils.download(fileURL());
	}

	public Path download(Path location) throws CurseException, IOException {
		return NIOUtils.download(fileURL(), location);
	}

	public Path downloadToDirectory(Path directory) throws CurseException, IOException {
		return NIOUtils.downloadToDirectory(fileURL(), directory);
	}

	@Override
	public String toString() {
		return getClass().getName() + "[id=" + id() + ",name=\"" + name() + "\"]";
	}

	@Override
	public int hashCode() {
		return id();
	}

	@Override
	public boolean equals(Object anotherObject) {
		if(anotherObject instanceof CurseFile) {
			return ((CurseFile) anotherObject).hashCode() == hashCode();
		}

		return false;
	}
}
