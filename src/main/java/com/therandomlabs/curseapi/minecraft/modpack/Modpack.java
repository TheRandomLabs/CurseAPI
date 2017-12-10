package com.therandomlabs.curseapi.minecraft.modpack;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseFileList;
import com.therandomlabs.curseapi.minecraft.MinecraftForge;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.util.MiscUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLCollectors;
import com.therandomlabs.utils.collection.TRLList;

public final class Modpack {
	private final String name;
	private final String version;
	private final String author;
	private final String description;
	private final String overrides;
	private final MinecraftVersion minecraftVersion;
	private final String forgeVersion;

	private TRLList<ModpackFileInfo> mods;
	private final TRLList<ModpackFileInfo> originalMods;
	private final TRLList<ModpackFileInfo> clientMods;
	private final TRLList<ModpackFileInfo> serverMods;

	private final TRLList<String> clientOnlyFiles;
	private final TRLList<String> serverOnlyFiles;

	public Modpack(String name, String version, String author, String description,
			MinecraftVersion minecraftVersion, String forgeVersion, ModpackFileInfo[] files)
			throws CurseException {
		this(name, version, author, description, "Overrides", minecraftVersion, forgeVersion,
				files);
	}

	public Modpack(String name, String version, String author, String description,
			String overrides, MinecraftVersion minecraftVersion, String forgeVersion,
			ModpackFileInfo[] files) throws CurseException {
		this.name = name;
		this.version = version;
		this.author = author;
		this.description = description;
		this.overrides = overrides;
		this.minecraftVersion = minecraftVersion;

		if(forgeVersion.equals("latest")) {
			this.forgeVersion = MinecraftForge.getLatestVersion(minecraftVersion);
		} else if(forgeVersion.equals("recommended")) {
			this.forgeVersion = MinecraftForge.getRecommendedVersion(minecraftVersion);
		} else {
			this.forgeVersion = forgeVersion;
		}

		mods = new TRLList<>(files);
		originalMods = mods.toImmutableList();
		clientMods = mods.stream().
				filter(file -> file.type != FileType.SERVER_ONLY).
				collect(TRLCollectors.toArrayList());
		serverMods = new ImmutableList<>(files).stream().
				filter(file -> file.type != FileType.CLIENT_ONLY).
				collect(TRLCollectors.toArrayList());

		final TRLList<String> clientOnlyFiles = new TRLList<>();
		clientMods.stream().filter(file -> file.type == FileType.CLIENT_ONLY).
				forEach(file -> clientOnlyFiles.addAll(file.relatedFiles));
		this.clientOnlyFiles = clientOnlyFiles.toImmutableList();

		final TRLList<String> serverOnlyFiles = new TRLList<>();
		serverMods.stream().filter(file -> file.type == FileType.SERVER_ONLY).
				forEach(file -> serverOnlyFiles.addAll(file.relatedFiles));
		this.serverOnlyFiles = serverOnlyFiles.toImmutableList();
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getFullName() {
		return name + " " + version;
	}

	public String getAuthor() {
		return author;
	}

	public String getDescription() {
		return description;
	}

	public String getOverrides() {
		return overrides;
	}

	public MinecraftVersion getMinecraftVersion() {
		return minecraftVersion;
	}

	public String getMinecraftVersionString() {
		return minecraftVersion.toString();
	}

	public String getForgeVersion() {
		return forgeVersion;
	}

	public String getModLoader() {
		return "forge-" + forgeVersion.split("-")[1];
	}

	public TRLList<ModpackFileInfo> getMods() {
		return mods;
	}

	public CurseFileList getCurseFileList() throws CurseException {
		return ModpackFileInfo.toCurseFileList(mods.toArray(new ModpackFileInfo[0]));
	}

	public TRLList<ModpackFileInfo> getOriginalMods() {
		return originalMods;
	}

	public TRLList<ModpackFileInfo> getClientMods() {
		return clientMods;
	}

	public TRLList<ModpackFileInfo> getServerMods() {
		return serverMods;
	}

	public void filterModsForClient() {
		filterMods(FileType.SERVER_ONLY);
	}

	public void filterModsForServer() {
		filterMods(FileType.CLIENT_ONLY);
	}

	private void filterMods(FileType typeToRemove) {
		final TRLList<ModpackFileInfo> mods = new TRLList<>(this.mods.size());

		for(ModpackFileInfo mod : this.mods) {
			if(mod.type != typeToRemove) {
				mods.add(mod);
			}
		}

		this.mods = mods;
	}

	public void removeMods(Collection<?> mods) {
		this.mods.removeAll(mods);
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

	public ModpackManifest toModpackInfo() {
		final ModpackManifest info = new ModpackManifest();

		info.manifestType = "minecraftModpack";
		info.manifestVersion = 1;
		info.name = name;
		info.version = version;
		info.author = author;
		info.description = description;
		info.files = mods.toArray(new ModpackFileInfo[0]);
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
		return MiscUtils.fromJson(manifest, ModpackManifest.class).toModpack();
	}
}
