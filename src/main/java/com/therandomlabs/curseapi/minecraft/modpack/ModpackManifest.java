package com.therandomlabs.curseapi.minecraft.modpack;

import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.CloneException;

public final class ModpackManifest implements Cloneable {
	public String manifestType;
	public int manifestVersion;
	public String name;
	public String version;
	public String author;
	public String description;
	public ModpackFileInfo[] files;
	public String overrides;
	public MinecraftInfo minecraft;
	public String optifineVersion = "";
	public double minimumRam = 2.5;
	public double recommendedRam = 4.0;

	@Override
	public ModpackManifest clone() {
		final ModpackManifest info = new ModpackManifest();

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
		return new Modpack(name, version, author, description, overrides, minecraft.version,
				minecraft.modLoaders[0].id.substring(6), files, optifineVersion, minimumRam,
				recommendedRam);
	}
}
