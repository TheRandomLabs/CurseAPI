package com.therandomlabs.curseapi.minecraft.modpack;

import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.util.CloneException;

public final class MinecraftInfo implements Cloneable {
	public MinecraftVersion version;
	public String libraries;
	public ModLoaderInfo[] modLoaders;

	@Override
	public MinecraftInfo clone() {
		final MinecraftInfo info = new MinecraftInfo();

		info.version = version;
		info.libraries = libraries;
		info.modLoaders = CloneException.tryClone(modLoaders);

		return info;
	}
}
