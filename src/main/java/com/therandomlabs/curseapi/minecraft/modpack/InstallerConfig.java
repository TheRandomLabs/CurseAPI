package com.therandomlabs.curseapi.minecraft.modpack;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.utils.number.NumberUtils;

public final class InstallerConfig {
	public String installTo;
	public String modpack;
	public boolean isServer;
	public boolean installForge = true;
	public boolean deleteOldForgeVersions = true;
	public boolean createEULA = true;
	public boolean createServerStarters = true;

	private boolean isProjectAndFileID;
	private boolean isURL;
	private boolean isPath;
	private boolean isInvalid;

	public InstallerConfig() {}

	public InstallerConfig(Path installTo, String modpack, boolean isServer,
			boolean installForge, boolean deleteOldForgeVersions, boolean createEULA,
			boolean createServerStarters) {
		this.installTo = installTo.toString();
		this.modpack = modpack;
		this.isServer = isServer;
		this.installForge = installForge;
		this.deleteOldForgeVersions = deleteOldForgeVersions;
		this.createEULA = createEULA;
		this.createServerStarters = createServerStarters;
	}

	public boolean isModpackProjectAndFileID() {
		getModpackType();
		return isProjectAndFileID;
	}

	public boolean isModpackURL() {
		getModpackType();
		return isURL;
	}

	public boolean isModpackPath() {
		getModpackType();
		return isPath;
	}

	public boolean isModpackInvalid() {
		getModpackType();
		return isInvalid;
	}

	private void getModpackType() {
		if(isProjectAndFileID || isURL || isPath || isInvalid) {
			return;
		}

		final String[] ids = modpack.split(":");
		if(ids.length == 2 && NumberUtils.parseInt(ids[0], 0) >= CurseAPI.MIN_PROJECT_ID &&
				NumberUtils.parseInt(ids[1], 0) > CurseAPI.MIN_PROJECT_ID) {
			isProjectAndFileID = true;
		} else {
			try {
				new URL(modpack);
				isURL = true;
			} catch(MalformedURLException ex) {
				try {
					Paths.get(modpack);
					isPath = true;
				} catch(InvalidPathException ex2) {
					isInvalid = true;
				}
			}
		}
	}
}
