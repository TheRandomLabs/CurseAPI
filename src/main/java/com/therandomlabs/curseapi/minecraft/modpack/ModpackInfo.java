package com.therandomlabs.curseapi.minecraft.modpack;

import java.util.Arrays;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.CloneException;
import com.therandomlabs.utils.collection.TRLCollectors;
import com.therandomlabs.utils.collection.TRLList;

public final class ModpackInfo implements Cloneable {
	public String manifestType;
	public int manifestVersion;
	public String name;
	public String version;
	public String author;
	public String description;
	public ModpackFileInfo[] files;
	public String overrides;
	public MinecraftInfo minecraft;

	@Override
	public ModpackInfo clone() {
		final ModpackInfo info = new ModpackInfo();

		info.manifestType = manifestType;
		info.manifestVersion = manifestVersion;
		info.name = name;
		info.version = version;
		info.author = author;
		info.description = description;
		info.files = CloneException.tryClone(files);
		info.overrides = overrides;
		info.minecraft = minecraft.clone();

		return info;
	}

	public TRLList<ModpackFileInfo> getClientMods() {
		return Arrays.stream(files).filter(file -> file.type != FileType.SERVER_ONLY).
				collect(TRLCollectors.toImmutableList());
	}

	public TRLList<ModpackFileInfo> getServerMods() {
		return Arrays.stream(files).filter(file -> file.type != FileType.CLIENT_ONLY).
				collect(TRLCollectors.toImmutableList());
	}

	public TRLList<String> getClientOnlyFiles() {
		final TRLList<String> clientOnlyFiles = new TRLList<>();
		Arrays.stream(files).filter(file -> file.type == FileType.CLIENT_ONLY).
				forEach(file -> clientOnlyFiles.addAll(file.relatedFiles));
		return clientOnlyFiles;
	}

	public TRLList<String> getServerOnlyFiles() {
		final TRLList<String> serverOnlyFiles = new TRLList<>();
		Arrays.stream(files).filter(file -> file.type == FileType.SERVER_ONLY).
				forEach(file -> serverOnlyFiles.addAll(file.relatedFiles));
		return serverOnlyFiles;
	}

	public Modpack toModpack() throws CurseException {
		return new Modpack(name, version, author, description, minecraft.version,
				minecraft.modLoaders[0].id.substring(5), ModpackFileInfo.toCurseFileList(files));
	}
}
