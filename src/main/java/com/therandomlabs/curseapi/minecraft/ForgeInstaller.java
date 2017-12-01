package com.therandomlabs.curseapi.minecraft;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import com.therandomlabs.curseapi.CurseException;

public class ForgeInstaller {
	private final Path path;

	public ForgeInstaller(File file) {
		this(file.toPath());
	}

	public ForgeInstaller(Path path) {
		this.path = path;
	}

	public Path getPath() {
		return path;
	}

	public static ForgeInstaller download(String forgeVersion, Path location)
			throws CurseException, IOException {
		return new ForgeInstaller(MinecraftForge.downloadInstaller(forgeVersion, location));
	}

	public static ForgeInstaller downloadToDirectory(String forgeVersion, Path directory)
			throws CurseException, IOException {
		return new ForgeInstaller(MinecraftForge.downloadInstallerToDirectory(forgeVersion,
				directory));
	}

	public static ForgeInstaller downloadLatest(MinecraftVersion version, Path location)
			throws CurseException, IOException {
		return download(MinecraftForge.getLatestVersion(version), location);
	}

	public static ForgeInstaller downloadLatestToDirectory(MinecraftVersion version, Path directory)
			throws CurseException, IOException {
		return downloadToDirectory(MinecraftForge.getLatestVersion(version), directory);
	}

	public static ForgeInstaller downloadRecommended(MinecraftVersion version, Path location)
			throws CurseException, IOException {
		return download(MinecraftForge.getRecommendedVersion(version), location);
	}

	public static ForgeInstaller downloadRecommendedToDirectory(MinecraftVersion version,
			Path directory) throws CurseException, IOException {
		return downloadToDirectory(MinecraftForge.getRecommendedVersion(version), directory);
	}

	//TODO
}
