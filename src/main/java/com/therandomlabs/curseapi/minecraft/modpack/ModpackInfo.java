package com.therandomlabs.curseapi.minecraft.modpack;

import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.CloneException;

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

	public Modpack toModpack() throws CurseException {
		return new Modpack(name, version, author, description, minecraft.version,
				minecraft.modLoaders[0].id.substring(5), ModpackFileInfo.toCurseFileList(files));
	}
}
