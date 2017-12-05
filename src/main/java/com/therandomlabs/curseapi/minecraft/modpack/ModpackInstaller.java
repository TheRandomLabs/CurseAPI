package com.therandomlabs.curseapi.minecraft.modpack;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseProject;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.curseapi.util.URLUtils;
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

		if(config.isModpackURL()) {
			if(CurseForge.isProject(config.modpack)) {
				url(config, CurseProject.fromURL(config.modpack).files().get(0).fileURL());
			} else {
				url(config, URLUtils.url(config.modpack));
			}
		}

		if(config.isModpackPath()) {
			final Path path = Paths.get(config.modpack);
			if(Files.isDirectory(path)) {
				directory(config, path);
			} else {
				zipFile(config, path);
			}
		}

		deleteTemporaryFiles();
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

		final Path manifest = Paths.get(directory.toString(), "manifest.json");
		final ModpackInfo modpack =
				new Gson().fromJson(NIOUtils.readFile(manifest), ModpackInfo.class);
		final List<String> filesToIgnore = config.isServer ?
				modpack.getClientOnlyFiles() : modpack.getServerOnlyFiles();


		/*final String[] clientOnlyFiles =
				manifest != null ? manifest.clientOnlyFiles : new String[0];
		Files.walkFileTree(location, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
					throws IOException {
				if(!shouldSkip(file)) {
					final Path newFile = file(location.relativize(file).toString());
					if(localModpack) {
						Files.copy(file, newFile, StandardCopyOption.REPLACE_EXISTING);
					} else {
						Files.move(file, newFile, StandardCopyOption.REPLACE_EXISTING);
					}
					data.addFile(newFile);
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path directory,
					BasicFileAttributes attributes) throws IOException {
				if(!shouldSkip(directory)) {
					final String relativized = location.relativize(directory).toString();
					if(!relativized.isEmpty()) {
						final Path newDirectory = file(relativized);
						if(!Files.isDirectory(newDirectory)) {
							Files.deleteIfExists(newDirectory);
							Files.createDirectory(newDirectory);
						}
					}
				}
				return FileVisitResult.CONTINUE;
			}

			private boolean shouldSkip(Path path) {
				if(config.installServer) {
					for(String file : clientOnlyFiles) {
						final Path clientOnlyFile = Paths.get(location.toString(), file);
						if(path.equals(clientOnlyFile) ||
								NIOUtils.isParent(clientOnlyFile, path)) {
							return true;
						}
					}
				}
				for(String file : config.fileExclusions) {
					final Path excludedFile = Paths.get(location.toString(), file);
					if(path.equals(excludedFile) || NIOUtils.isParent(excludedFile, path)) {
						return true;
					}
				}
				return false;
			}
		});

		return false;*/
	}

	public static void deleteTemporaryFiles() {
		for(int i = 0; i < temporaryFiles.size(); i++) {
			try {
				if(Files.isDirectory(temporaryFiles.get(i))) {
					NIOUtils.deleteDirectory(temporaryFiles.get(i--));
				} else {
					Files.deleteIfExists(temporaryFiles.get(i--));
				}
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
