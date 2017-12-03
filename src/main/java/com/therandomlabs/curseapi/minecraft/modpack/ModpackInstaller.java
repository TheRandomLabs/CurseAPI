package com.therandomlabs.curseapi.minecraft.modpack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.therandomlabs.utils.io.NIOUtils;

public final class ModpackInstaller {
	private static final List<Path> temporaryFiles = new ArrayList<>();

	private ModpackInstaller() {}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(ModpackInstaller::deleteTemporaryFiles));
	}

	public static void installModpack(InstallerConfig config) {

	}

	public static void deleteTemporaryFiles() {
		for(Path path : temporaryFiles) {
			try {
				if(Files.isDirectory(path)) {
					NIOUtils.deleteDirectory(path);
				} else {
					Files.deleteIfExists(path);
				}
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
