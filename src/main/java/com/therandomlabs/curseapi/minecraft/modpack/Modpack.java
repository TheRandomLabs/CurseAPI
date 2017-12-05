package com.therandomlabs.curseapi.minecraft.modpack;

import java.io.IOException;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseFileList;
import com.therandomlabs.curseapi.minecraft.MinecraftForge;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.util.MiscUtils;
import com.therandomlabs.utils.collection.TRLList;

public final class Modpack {
	private final String name;
	private final String version;
	private final String author;
	private final String description;
	private final MinecraftVersion minecraftVersion;
	private final String forgeVersion;

	private final CurseFileList files;
	private final CurseFileList clientMods;
	private final CurseFileList serverMods;

	private final TRLList<String> clientOnlyFiles;
	private final TRLList<String> serverOnlyFiles;

	public Modpack(String name, String version, String author, String description,
			MinecraftVersion minecraftVersion, String forgeVersion, CurseFileList files)
			throws CurseException {
		this.name = name;
		this.version = version;
		this.author = author;
		this.description = description;
		this.minecraftVersion = minecraftVersion;

		if(forgeVersion.equals("latest")) {
			this.forgeVersion = MinecraftForge.getLatestVersion(minecraftVersion);
		} else if(forgeVersion.equals("recommended")) {
			this.forgeVersion = MinecraftForge.getRecommendedVersion(minecraftVersion);
		} else {
			this.forgeVersion = forgeVersion;
		}

		this.files = files;
		clientMods = files.filter(file -> ((ModpackFile) file).getType() != FileType.SERVER_ONLY);
		serverMods = files.filter(file -> ((ModpackFile) file).getType() != FileType.CLIENT_ONLY);

		final TRLList<String> clientOnlyFiles = new TRLList<>();
		clientMods.stream().filter(file -> ((ModpackFile) file).getType() == FileType.CLIENT_ONLY).
				forEach(file -> clientOnlyFiles.addAll(((ModpackFile) file).getRelatedFiles()));
		this.clientOnlyFiles = clientOnlyFiles.toImmutableList();

		final TRLList<String> serverOnlyFiles = new TRLList<>();
		serverMods.stream().filter(file -> ((ModpackFile) file).getType() == FileType.SERVER_ONLY).
				forEach(file -> serverOnlyFiles.addAll(((ModpackFile) file).getRelatedFiles()));
		this.serverOnlyFiles = serverOnlyFiles.toImmutableList();
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getAuthor() {
		return author;
	}

	public String getDescription() {
		return description;
	}

	public MinecraftVersion getMinecraftVersion() {
		return minecraftVersion;
	}

	public String getForgeVersion() {
		return forgeVersion;
	}

	public String getModLoader() {
		return "forge-" + forgeVersion.split("-")[1];
	}

	public CurseFileList getMods() {
		return files;
	}

	public CurseFileList getClientMods() {
		return clientMods;
	}

	public CurseFileList getServerMods() {
		return serverMods;
	}

	public TRLList<String> getClientOnlyFiles() {
		return clientOnlyFiles;
	}

	public TRLList<String> getServerOnlyFiles() {
		return serverOnlyFiles;
	}

	public String toJson() {
		return new Gson().toJson(toModpackInfo());
	}

	public String toPrettyJson() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(toModpackInfo());
	}

	public String toPrettyJsonWithTabs() {
		return toPrettyJson().replaceAll("  ", "\t");
	}

	public ModpackInfo toModpackInfo() {
		final ModpackInfo info = new ModpackInfo();

		info.manifestType = "minecraftModpack";
		info.manifestVersion = 1;
		info.name = name;
		info.version = version;
		info.author = author;
		info.description = description;
		info.files = ModpackFileInfo.fromCurseFiles(files);
		info.overrides = "Overrides";
		info.minecraft = toMinecraftInfo();

		return info;
	}

	public MinecraftInfo toMinecraftInfo() {
		final MinecraftInfo info = new MinecraftInfo();

		info.version = minecraftVersion;
		info.libraries = "libraries";
		info.modLoaders = toModLoaderInfos();

		return info;
	}

	public ModLoaderInfo[] toModLoaderInfos() {
		final ModLoaderInfo info = new ModLoaderInfo();

		info.id = getModLoader();
		info.primary = true;

		return new ModLoaderInfo[] {info};
	}

	public static Modpack fromManifest(Path manifest) throws CurseException, IOException {
		return MiscUtils.fromJson(manifest, ModpackInfo.class).toModpack();
	}
}
