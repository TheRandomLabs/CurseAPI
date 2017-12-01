package com.therandomlabs.curseapi.minecraft.modpack;

public final class ModLoaderInfo implements Cloneable {
	public String id;
	public boolean primary;

	@Override
	public ModLoaderInfo clone() {
		final ModLoaderInfo info = new ModLoaderInfo();

		info.id = id;
		info.primary = primary;

		return info;
	}
}
