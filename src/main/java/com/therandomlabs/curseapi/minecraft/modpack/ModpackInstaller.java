package com.therandomlabs.curseapi.minecraft.modpack;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.utils.io.NIOUtils;
import com.therandomlabs.utils.misc.Assertions;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public final class ModpackInstaller {
	private static final List<Path> temporaryFiles = new ArrayList<>();

	private ModpackInstaller() {}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(ModpackInstaller::deleteTemporaryFiles));
	}

	public static void installModpack(InstallerConfig config)
			throws CurseException, IOException, ZipException {
		if(config.isModpackInvalid()) {
			throw new CurseException("Invalid modpack: " + config.modpack);
		}

		if(config.isModpackProjectAndFileID()) {
			final String[] ids = config.modpack.split(":");
			url(config, CurseForge.getFileURL(Integer.parseInt(ids[0]), Integer.parseInt(ids[1])));
		}
	}

	private static void url(InstallerConfig config, URL url)
			throws CurseException, IOException, ZipException {
		final Path location = Paths.get(NIOUtils.TEMP_DIRECTORY.toString(),
				String.valueOf(System.nanoTime()));
		temporaryFiles.add(location);
		NIOUtils.download(url, location);

		zipFile(config, location);
	}

	private static void zipFile(InstallerConfig config, Path zipFile)
			throws CurseException, IOException, ZipException {
		Assertions.file(zipFile);

		final ZipFile zip = new ZipFile(zipFile.toFile());
		if(!zip.isValidZipFile()) {
			throw new CurseException("Invalid zip file");
		}

		final Path location = Paths.get(NIOUtils.TEMP_DIRECTORY.toString(),
				zipFile.getFileName().toString() + System.nanoTime());
		temporaryFiles.add(location);

		zip.extractAll(location.toString());

		directory(config, location);
	}

	private static void directory(InstallerConfig config, Path directory)
			throws CurseException, IOException {
		//TODO delete old mods, install new ones
		//iterate through configs, replace strings
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
