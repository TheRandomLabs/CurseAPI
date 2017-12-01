package com.therandomlabs.curseapi.minecraft.modpack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseFileList;
import com.therandomlabs.curseapi.minecraft.MinecraftForge;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;

public final class Modpack {
	private final String name;
	private final String version;
	private final String author;
	private final String description;
	private final MinecraftVersion minecraftVersion;
	private final String forgeVersion;
	private final CurseFileList files;

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

	public CurseFileList getFiles() {
		return files;
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
}
