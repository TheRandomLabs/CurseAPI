package com.therandomlabs.curseapi.minecraft.modpack;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.gson.Gson;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseProject;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.curseapi.minecraft.modpack.InstallerData.ModData;
import com.therandomlabs.curseapi.util.CurseEventHandling;
import com.therandomlabs.curseapi.util.MiscUtils;
import com.therandomlabs.curseapi.util.ThreadWithIndexValues;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.io.IOConstants;
import com.therandomlabs.utils.io.NIOUtils;
import com.therandomlabs.utils.misc.Assertions;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.wrapper.Wrapper;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public final class ModpackInstaller {
	public static final String MODPACK_NAME = "::MODPACK_NAME::";
	public static final String MODPACK_VERSION = "::MODPACK_VERSION::";
	public static final String FULL_MODPACK_NAME = "::FULL_MODPACK_NAME::";
	public static final String MODPACK_AUTHOR = "::MODPACK_AUTHOR::";

	private static final List<Path> temporaryFiles = new ArrayList<>();

	private ModpackInstaller() {}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(ModpackInstaller::deleteTemporaryFiles));
	}

	public static void installModpack(Path config)
			throws CurseException, IOException, ZipException {
		installModpack(MiscUtils.fromJson(config, InstallerConfig.class));
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
				config.isLocal = true;
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
		final Path manifestPath = Paths.get(directory.toString(), "manifest.json");
		final Modpack modpack = Modpack.fromManifest(manifestPath);
		final InstallerData data = new InstallerData();

		final Path mods = Paths.get(config.installTo, "mods");
		if(!Files.exists(mods)) {
			Files.createDirectories(mods);
		}

		if(config.isServer) {
			modpack.filterModsForServer();
		} else {
			modpack.filterModsForClient();
		}

		deleteOldFiles(config, data, modpack);
		copyNewFiles(directory, config, data, modpack);
		downloadMods(config, data, modpack);
		installForge(config, data, modpack);
		createEULAAndServerStarters(config, data, modpack);

		//Remove empty directories - most of them are probably left over from previous
		//modpack versions
		NIOUtils.deleteDirectory(Paths.get(config.installTo), NIOUtils.DELETE_EMPTY_DIRECTORIES);

		//Write data
		Files.write(Paths.get(config.installTo, config.dataFile),
				new ImmutableList<>(new Gson().toJson(data)));
	}

	//TODO config.modSources

	private static void deleteOldFiles(InstallerConfig config, InstallerData data, Modpack modpack)
			throws CurseException, IOException {
		final Path dataPath = Paths.get(config.installTo, config.dataFile);
		if(!Files.exists(dataPath)) {
			return;
		}

		final InstallerData oldData = MiscUtils.fromJson(dataPath, InstallerData.class);
		if(oldData.forgeVersion.equals(modpack.getForgeVersion())) {
			config.shouldInstallForge = false;
		} else if(!config.isServer && config.deleteOldForgeVersion) {
			NIOUtils.deleteDirectoryIfExists(Paths.get(
					config.installTo,
					"versions",
					oldData.minecraftVersion + "-forge" + oldData.forgeVersion
			));
		}

		getModsToKeep(config, oldData, data, modpack);

		for(InstallerData.ModData mod : oldData.mods) {
			CurseEventHandling.forEach(handler -> handler.deleting(mod.location));

			final Path path = Paths.get(config.installTo, mod.location);
			if(!Files.deleteIfExists(path)) {
				Files.deleteIfExists(Paths.get(config.installTo, "mods",
						oldData.minecraftVersion, path.getFileName().toString()));
			}

			for(String relatedFile : mod.relatedFiles) {
				Files.deleteIfExists(Paths.get(config.installTo, relatedFile));
			}
		}

		for(String file : oldData.installedFiles) {
			CurseEventHandling.forEach(handler -> handler.deleting(file));
			Files.deleteIfExists(Paths.get(config.installTo, file));
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	private static void getModsToKeep(InstallerConfig config, InstallerData oldData,
			InstallerData data, Modpack modpack) {
		if(config.redownloadAll) {
			return;
		}

		//Get mods to keep

		final List<InstallerData.ModData> modsToKeep = new ArrayList<>();
		for(InstallerData.ModData oldMod : oldData.mods) {
			//InstallerData.ModData.equals(ModpackFileInfo) will return true if fileID is the same
			if(modpack.getMods().contains(oldMod)) {
				Path location = Paths.get(config.installTo, oldMod.location);
				final Path location2 = Paths.get(config.installTo, "mods",
						oldData.minecraftVersion, location.getFileName().toString());

				if(Files.exists(location2)) {
					oldMod.location =
							"mods/" + oldData.minecraftVersion + "/" + location.getFileName();
					location = location2;
				}

				if(Files.exists(location)) {
					modsToKeep.add(oldMod);
					data.mods.add(oldMod);
				}
			}
		}

		//Remove mods to keep from oldData.mods because later we're going to be deleting
		//all files from oldData
		oldData.mods.removeAll(modsToKeep);
		//Remove them from the modpack as well so they aren't redownloaded
		modpack.removeMods(modsToKeep);
	}

	private static void copyNewFiles(Path modpackLocation, InstallerConfig config,
			InstallerData data, Modpack modpack)
			throws IOException {
		final Path overrides = Paths.get(modpackLocation.toString(), modpack.getOverrides());
		final Path installTo = Paths.get(config.installTo);
		final List<String> filesToIgnore =
				config.isServer ? modpack.getClientOnlyFiles() : modpack.getServerOnlyFiles();

		Files.walkFileTree(overrides, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
					throws IOException {
				final Path relativized = relativize(file);

				if(!shouldSkip(relativized)) {
					final Path newFile = Paths.get(installTo.toString(), relativized.toString());

					if(Files.isDirectory(newFile)) {
						NIOUtils.deleteDirectory(newFile);
					}

					final String name = file.getFileName().toString();
					if(name.endsWith(".cfg") || name.endsWith(".json") || name.endsWith(".txt")) {
						final String toWrite = NIOUtils.readFile(file).
								replaceAll(MODPACK_NAME, modpack.getName()).
								replaceAll(MODPACK_VERSION, modpack.getVersion()).
								replaceAll(FULL_MODPACK_NAME, modpack.getFullName()).
								replaceAll(MODPACK_AUTHOR, modpack.getAuthor()) +
								System.lineSeparator();

						NIOUtils.write(newFile, toWrite);
					} else {
						if(config.isLocal) {
							Files.copy(file, newFile, StandardCopyOption.REPLACE_EXISTING);
						} else {
							Files.move(file, newFile, StandardCopyOption.REPLACE_EXISTING);
						}
					}

					data.installedFiles.add(StringUtils.replaceAll(relativized.toString(),
							IOConstants.PATH_SEPARATOR, IOConstants.PATH_SEPARATOR_UNIX));
				}

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path directory,
					BasicFileAttributes attributes) throws IOException {
				directory = relativize(directory);

				if(!shouldSkip(directory)) {
					final Path path = Paths.get(installTo.toString(), directory.toString());

					if(Files.exists(path) && !Files.isDirectory(path)) {
						Files.delete(path);
					}

					if(!Files.exists(path)) {
						Files.createDirectory(path);
					}
				}

				return FileVisitResult.CONTINUE;
			}

			private boolean shouldSkip(Path path) {
				for(String fileName : filesToIgnore) {
					final Path toIgnore = Paths.get("config", fileName);
					if(path.equals(toIgnore) || NIOUtils.isParent(toIgnore, path)) {
						return true;
					}
				}
				return false;
			}

			private Path relativize(Path path) {
				return overrides.relativize(path).normalize();
			}
		});
	}

	private static void downloadMods(InstallerConfig config, InstallerData data, Modpack modpack)
			throws CurseException, IOException {
		if(modpack.getMods().isEmpty()) {
			return;
		}

		final int maxThreads = config.threads > 0 ? config.threads : CurseAPI.getMaximumThreads();
		final int threadCount = modpack.getMods().size() < maxThreads ?
				modpack.getMods().size() : maxThreads;
		final Thread[] threads = new Thread[threadCount];
		final int increment = modpack.getMods().size() / threadCount;

		final Wrapper<Exception> exception = new Wrapper<>();
		final AtomicInteger count = new AtomicInteger();

		for(int i = 0, j = 0; i < threadCount; i++, j += increment) {
			threads[i] = new ThreadWithIndexValues(i, j, indexes -> {
				final int threadIndex = indexes.value1();
				final int startIndex = indexes.value2();

				final int endIndex;
				if(threadIndex == threadCount - 1) {
					endIndex = modpack.getMods().size();
				} else {
					endIndex = startIndex + increment;
				}

				try {
					for(int fileIndex = startIndex; fileIndex < endIndex; fileIndex++) {
						if(exception.hasValue()) {
							return;
						}

						downloadMod(config, data, modpack.getMods().get(fileIndex),
								count.incrementAndGet(), modpack.getMods().size());
					}
				} catch(CurseException | IOException ex) {
					exception.set(ex);
				}
			});
			threads[i].start();
		}

		try {
			for(Thread thread : threads) {
				thread.join();
			}
		} catch(InterruptedException ex) {
			throw new CurseException(ex);
		}

		if(exception.hasValue()) {
			if(exception.get() instanceof CurseException) {
				throw (CurseException) exception.get();
			}
			throw (IOException) exception.get();
		}
	}

	private static void downloadMod(InstallerConfig config, InstallerData data,
			ModpackFileInfo mod, int count, int total) throws CurseException, IOException {
		CurseEventHandling.forEach(handler -> handler.downloadingMod(mod.title, count, total));

		final URL url = CurseForge.getFileURL(mod.projectID, mod.fileID);

		final Path location = Paths.get(config.installTo).relativize(
				NIOUtils.downloadToDirectory(url, Paths.get(config.installTo, "mods")));

		final ModData modData = new ModData();

		modData.projectID = mod.projectID;
		modData.fileID = mod.fileID;
		modData.location = StringUtils.replaceAll(location.toString(), IOConstants.PATH_SEPARATOR,
				IOConstants.PATH_SEPARATOR_UNIX);
		modData.relatedFiles = mod.relatedFiles;

		CurseEventHandling.forEach(handler -> handler.downloadedMod(mod.title,
				location.getFileName().toString(), count));

		data.mods.add(modData);
	}

	private static void installForge(InstallerConfig config, InstallerData data, Modpack modpack)
			throws IOException {
		data.minecraftVersion = modpack.getMinecraftVersion().toString();
		data.forgeVersion = modpack.getForgeVersion();

		if(!config.shouldInstallForge) {
			return;
		}

		//TODO
	}

	private static void createEULAAndServerStarters(InstallerConfig config, InstallerData data,
			Modpack modpack) throws IOException {
		if(!config.isServer) {
			return;
		}

		//TODO
	}

	//public static void createZip(Path directory, Path zip) throws IOException {

	public static void deleteTemporaryFiles() {
		for(int i = 0; i < temporaryFiles.size(); i++) {
			try {
				if(Files.isDirectory(temporaryFiles.get(i))) {
					NIOUtils.deleteDirectoryIfExists(temporaryFiles.get(i--));
				} else {
					Files.deleteIfExists(temporaryFiles.get(i--));
				}
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	//TODO excluded project IDs, fix path separators
}
