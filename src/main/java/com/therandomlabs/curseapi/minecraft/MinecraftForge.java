package com.therandomlabs.curseapi.minecraft;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.utils.io.NIOUtils;

public final class MinecraftForge {
	public static final String MC_VERSION = "::MC_VERSION::";
	public static final String PAGE_URL =
			"http://files.minecraftforge.net/maven/net/minecraftforge/forge/index_" +
			MC_VERSION + ".html";

	public static final String FORGE_VERSION = "::FORGE_VERSION::";
	public static final String INSTALLER_URL =
			"https://files.minecraftforge.net/maven/net/minecraftforge/forge/" +
			FORGE_VERSION + "/forge-" + FORGE_VERSION + "-installer.jar";

	private MinecraftForge() {}

	public static URL getPageURLFromVersion(String version) throws CurseException {
		return getPageURLFromVersion(MinecraftVersion.fromString(version));
	}

	public static URL getPageURLFromVersion(MinecraftVersion version) throws CurseException {
		return URLUtils.url(PAGE_URL.replaceAll(MC_VERSION, version.toString()));
	}

	public static String getLatestVersion(String version) throws CurseException {
		return getLatestVersion(MinecraftVersion.fromString(version));
	}

	public static String getLatestVersion(MinecraftVersion version) throws CurseException {
		return DocumentUtils.getValue(getPageURLFromVersion(version),
				"class=title;tag=small;text").replaceAll(" - ", "-");
	}

	public static String getRecommendedVersion(String version) throws CurseException {
		return getRecommendedVersion(MinecraftVersion.fromString(version));
	}

	public static String getRecommendedVersion(MinecraftVersion version) throws CurseException {
		return DocumentUtils.getValue(getPageURLFromVersion(version),
				"class=title=1;tag=small;text").replaceAll(" - ", "-");
	}

	public static URL getInstallerURL(String forgeVersion) throws CurseException {
		return URLUtils.url(INSTALLER_URL.replaceAll(FORGE_VERSION, forgeVersion));
	}

	public static Path downloadInstaller(String forgeVersion, Path location)
			throws CurseException, IOException {
		return NIOUtils.download(getInstallerURL(forgeVersion), location);
	}

	public static Path downloadInstallerToDirectory(String forgeVersion, Path directory)
			throws CurseException, IOException {
		return NIOUtils.downloadToDirectory(getInstallerURL(forgeVersion), directory);
	}

	public static String getInstalledDirectoryName(String minecraftVersion, String forgeVersion) {
		return minecraftVersion + "-forge" + forgeVersion;
	}

	public static String getInstalledDirectoryName(MinecraftVersion minecraftVersion,
			String forgeVersion) {
		return getInstalledDirectoryName(minecraftVersion.toString(), forgeVersion);
	}
}
