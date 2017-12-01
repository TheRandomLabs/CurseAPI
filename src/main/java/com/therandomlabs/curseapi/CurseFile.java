package com.therandomlabs.curseapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.curseapi.widget.FileInfo;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.io.NIOUtils;
import com.therandomlabs.utils.network.NetworkUtils;

//TODO Changelog, Uploaded by, Additional Files
public class CurseFile {
	private final CurseProject project;
	private final FileInfo widgetInfo;

	private final TRLList<String> gameVersions;
	private final MinecraftVersion minecraftVersion;
	private final List<MinecraftVersion> minecraftVersions;

	private final URL url;

	private URL fileURL;

	protected CurseFile(CurseProject project, FileInfo widgetInfo) throws CurseException {
		this.project = project;
		this.widgetInfo = widgetInfo;

		gameVersions = new ImmutableList<>(widgetInfo.versions);

		if(project.game() == Game.MINECRAFT) {
			minecraftVersion = MinecraftVersion.fromString(widgetInfo.version);
			minecraftVersions = CollectionUtils.convert(gameVersions,
					MinecraftVersion::fromString).toImmutableList();
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
		if(fileURL == null) {
			fileURL = URLUtils.url(DocumentUtils.getValue(url,
					"class=fa-icon-download;redirectAbsUrl=href"));
		}

		return fileURL;
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
		return ZonedDateTime.parse(widgetInfo.uploaded_at);
	}

	public String fileSize() {
		return widgetInfo.filesize;
	}

	public int downloads() {
		return widgetInfo.downloads;
	}

	public CurseProject project() {
		return project;
	}

	public FileInfo widgetInfo() {
		return widgetInfo.clone();
	}

	public InputStream download() throws IOException {
		return NetworkUtils.download(fileURL);
	}

	public Path download(Path location) throws IOException {
		return NIOUtils.download(fileURL, location);
	}

	public Path downloadToDirectory(Path directory) throws IOException {
		return NIOUtils.downloadToDirectory(fileURL, directory);
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
