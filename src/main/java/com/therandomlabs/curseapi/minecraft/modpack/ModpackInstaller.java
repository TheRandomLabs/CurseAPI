package com.therandomlabs.curseapi.minecraft.modpack;

import static com.therandomlabs.utils.logging.Logging.getLogger;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
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
import com.google.gson.stream.MalformedJsonException;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseProject;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.curseapi.minecraft.MinecraftForge;
import com.therandomlabs.curseapi.minecraft.modpack.InstallerData.ModData;
import com.therandomlabs.curseapi.util.CurseEventHandler;
import com.therandomlabs.curseapi.util.CurseEventHandling;
import com.therandomlabs.curseapi.util.MiscUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.concurrent.ThreadUtils;
import com.therandomlabs.utils.io.NIOUtils;
import com.therandomlabs.utils.misc.Assertions;
import com.therandomlabs.utils.misc.Timer;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

//https://github.com/google/gson/issues/395 may occur
public final class ModpackInstaller {
	public static final String MINECRAFT_VERSION = "::MINECRAFT_VERSION::";
	public static final String MODPACK_NAME = "::MODPACK_NAME::";
	public static final String MODPACK_VERSION = "::MODPACK_VERSION::";
	public static final String FULL_MODPACK_NAME = "::FULL_MODPACK_NAME::";
	public static final String MODPACK_AUTHOR = "::MODPACK_AUTHOR::";

	private static final List<Path> temporaryFiles = new ArrayList<>();

	private ModpackInstaller() {}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(ModpackInstaller::deleteTemporaryFiles));
	}

	public static void createZip(Path path, Path zipLocation)
			throws CurseException, IOException, ZipException {
		createZip(path, zipLocation, "cfg", "json", "txt");
	}

	public static void createZip(Path path, Path zipLocation,
			String... variableFileExtensions) throws CurseException, IOException, ZipException {
		final Path directory;
		final Path manifest;

		if(Files.isDirectory(path)) {
			directory = path;
			manifest = directory.resolve("manifest.json");
		} else {
			directory = path.getParent();
			manifest = path;
		}

		final Modpack modpack = Modpack.from(manifest);

		final Path overrides = directory.resolve(modpack.getOverrides());

		final Path copyTo = NIOUtils.TEMP_DIRECTORY.get().
				resolve(name(zipLocation) + System.nanoTime()).resolve(modpack.getOverrides());

		final Path copyToParent = copyTo.getParent();
		Files.createDirectory(copyToParent);
		temporaryFiles.add(copyToParent);

		final Path newManifest = copyToParent.resolve("manifest.json");

		Files.copy(manifest, newManifest, StandardCopyOption.REPLACE_EXISTING);

		Files.walkFileTree(overrides, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
					throws IOException {
				copyFile(overrides, copyTo, file, modpack, variableFileExtensions);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path directory,
					BasicFileAttributes attributes) throws IOException {
				visitDirectory(overrides, copyTo, directory);
				return FileVisitResult.CONTINUE;
			}
		});

		createZip(newManifest, copyTo, new ZipFile(zipLocation.toFile()));

		deleteTemporaryFiles();
	}


	static void copyFile(Path overrides, Path copyTo, Path file, Modpack modpack,
			String[] variableFileExtensions) throws IOException {
		final Path relativized = relativize(overrides, file);
		final Path newFile =  copyTo.resolve(relativized.toString());
		final String name = name(file);

		//TODO logging

		boolean variablesReplaced = shouldReplaceVariables(variableFileExtensions, name);
		if(variablesReplaced) {
			variablesReplaced = replaceVariablesAndCopy(file, newFile, modpack);
		}

		if(!variablesReplaced) {
			Files.copy(file, newFile, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	static void visitDirectory(Path overrides, Path copyTo, Path directory) throws IOException {
		copyTo = copyTo.resolve(relativize(overrides, directory).toString());

		//Make sure copyTo is a directory that exists

		if(Files.exists(copyTo) && !Files.isDirectory(copyTo)) {
			Files.delete(copyTo);
		}

		if(!Files.exists(copyTo)) {
			Files.createDirectory(copyTo);
		}
	}

	private static void createZip(Path manifest, Path overrides, ZipFile zipFile)
			throws ZipException {
		final ZipParameters parameters = new ZipParameters();

		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

		zipFile.addFile(manifest.toFile(), parameters);
		zipFile.addFolder(overrides.toFile(), parameters);
	}

	public static void installModpack(Path config)
			throws CurseException, IOException, ZipException {
		installModpack(MiscUtils.fromJson(config, InstallerConfig.class));
	}

	public static void installModpack(InstallerConfig config)
			throws CurseException, IOException, ZipException {
		config.getInstallTo();

		if(config.isModpackInvalid()) {
			throw new CurseException("Invalid modpack: " + config.modpack);
		}

		if(config.isModpackProjectAndFileID()) {
			final String[] ids = config.modpack.split(":");
			url(config, CurseForge.getFileURL(Integer.parseInt(ids[0]), Integer.parseInt(ids[1])));
		}

		if(config.isModpackURL()) {
			if(CurseForge.isProject(config.modpack)) {
				//Latest version of modpack
				url(config, CurseProject.fromURL(config.modpack).files().get(0).fileURL());
			} else {
				//Direct link
				url(config, URLUtils.url(config.modpack));
			}
		}

		if(config.isModpackPath()) {
			final Path path = Paths.get(config.modpack);
			if(Files.isDirectory(path)) {
				config.shouldKeepModpack = true;
				directory(config, path);
			} else {
				zipFile(config, path);
			}
		}

		deleteTemporaryFiles();
	}

	private static void url(InstallerConfig config, URL url)
			throws CurseException, IOException, ZipException {
		final Path downloadedModpack = NIOUtils.TEMP_DIRECTORY.get().
				resolve("CurseAPI_Modpack.zip" + System.nanoTime());

		temporaryFiles.add(downloadedModpack);

		CurseEventHandling.forEach(handler -> handler.downloadingFromURL(url));
		NIOUtils.download(url, downloadedModpack);

		zipFile(config, downloadedModpack);
	}

	private static void zipFile(InstallerConfig config, Path modpackZip)
			throws CurseException, IOException, ZipException {
		Assertions.file(modpackZip);

		final ZipFile zip = new ZipFile(modpackZip.toFile());
		if(!zip.isValidZipFile()) {
			throw new CurseException("Invalid zip file");
		}

		Path extractLocation = NIOUtils.TEMP_DIRECTORY.get().
				resolve(name(modpackZip) + System.nanoTime());

		temporaryFiles.add(extractLocation);

		zip.extractAll(extractLocation.toString());

		final List<Path> files = NIOUtils.list(extractLocation);
		if(files.size() == 1 && Files.isDirectory(files.get(0))) {
			extractLocation = files.get(0);
		}

		directory(config, extractLocation);
	}

	private static void directory(InstallerConfig config, Path directory)
			throws CurseException, IOException {
		final Path manifestPath = directory.resolve("manifest.json");
		final Modpack modpack = Modpack.from(manifestPath);
		final InstallerData data = new InstallerData();

		final Path modsDirectory = installTo(config, "mods");
		if(!Files.exists(modsDirectory)) {
			Files.createDirectories(modsDirectory);
		}

		if(config.isServer) {
			modpack.removeClientOnlyMods();
		} else {
			modpack.removeServerOnlyMods();
		}

		final Path dataFile = installTo(config, config.dataFile);

		Assertions.positive(config.autosaveInterval, "autosaveInterval", false);
		final Timer autosaver = new Timer(() -> {
			try {
				NIOUtils.write(dataFile, new Gson().toJson(data));
				CurseEventHandling.forEach(CurseEventHandler::autosavedInstallerData);
			} catch(CurseException | IOException ex) {
				//Doesn't matter; this is just autosave anyway
				ex.printStackTrace();
			}
		}, config.autosaveInterval);

		try {
			deleteOldFiles(config, data, modpack, autosaver);
		} catch(MalformedJsonException ex) {
			getLogger().warning("Invalid data file!");
		}

		iterateModSources(config, data, modpack);
		copyNewFiles(directory, config, data, modpack);
		downloadMods(config, data, modpack);
		installForge(config, data, modpack);
		createEULAAndServerStarters(config, data, modpack);

		//Remove empty directories - most of them are probably left over from previous
		//modpack versions
		NIOUtils.deleteDirectory(Paths.get(config.installTo), NIOUtils.DELETE_EMPTY_DIRECTORIES);

		//Last save
		autosaver.stop();
		NIOUtils.write(dataFile, new Gson().toJson(data));

		deleteTemporaryFiles();
	}

	private static void deleteOldFiles(InstallerConfig config, InstallerData data, Modpack modpack,
			Timer autosaver) throws CurseException, IOException {
		final Path dataPath = installTo(config, config.dataFile);
		if(!Files.exists(dataPath)) {
			autosaver.start();
			return;
		}

		final InstallerData oldData = MiscUtils.fromJson(dataPath, InstallerData.class);
		autosaver.start();

		if(oldData.forgeVersion == null) {
			getLogger().warning("Invalid data file!");
			return;
		}

		if(oldData.forgeVersion.equals(modpack.getForgeVersion())) {
			//Forge should not be installed because it is already on the correct version
			config.shouldInstallForge = false;
		} else if(!config.isServer && config.deleteOldForgeVersion) {
			//Delete old Forge
			final Path oldForge = installTo(
					config, "versions", MinecraftForge.getInstalledDirectoryName(
							oldData.minecraftVersion,
							oldData.forgeVersion
					)
			);

			CurseEventHandling.forEach(handler -> handler.deleting(toString(oldForge)));

			NIOUtils.deleteDirectoryIfExists(oldForge);
		}

		removeModsToKeepFromData(config, oldData, data, modpack);

		//Deleting old mods that are no longer needed
		for(InstallerData.ModData mod : oldData.mods) {
			CurseEventHandling.forEach(handler -> handler.deleting(mod.location));

			final Path modLocation = installTo(config, mod.location);
			if(!Files.deleteIfExists(modLocation)) {
				//Some mods are moved to mods/<version>/mod.jar
				Files.deleteIfExists(
						installTo(config, "mods", oldData.minecraftVersion, name(modLocation)));
			}

			//Deleting related files
			for(String relatedFile : mod.relatedFiles) {
				Files.deleteIfExists(installTo(config, relatedFile));
			}
		}

		//Deleting old installedFiles - if they're needed, they'll be copied back anyway
		for(String file : oldData.installedFiles) {
			CurseEventHandling.forEach(handler -> handler.deleting(file));
			Files.deleteIfExists(installTo(config, file));
		}
	}

	private static void removeModsToKeepFromData(InstallerConfig config, InstallerData oldData,
			InstallerData data, Modpack modpack) {
		if(config.redownloadAll) {
			return;
		}

		final List<InstallerData.ModData> modsToKeep = new ArrayList<>();
		for(InstallerData.ModData mod : oldData.mods) {
			//This mod shouldn't be kept since its project ID has been excluded
			if(ArrayUtils.contains(config.excludeProjectIDs, mod.projectID)) {
				continue;
			}

			if(modpackContains(modpack, mod)) {
				Path modLocation = installTo(config, mod.location);
				final Path modLocation2 =
						installTo(config, "mods", oldData.minecraftVersion, name(modLocation));

				boolean exists = Files.exists(modLocation2);

				if(exists) {
					//Some mods are moved to mods/<version>/mod.jar
					mod.location = "mods/" + oldData.minecraftVersion + "/" + name(modLocation);
				} else {
					exists = Files.exists(modLocation);
				}

				if(exists) {
					modsToKeep.add(mod);
					data.mods.add(mod);
				}
			}
		}

		//Remove from oldData so all the old mods can be safely removed
		oldData.mods.removeAll(modsToKeep);
		//Remove from modpack so they aren't redownloaded
		modpack.removeInstallerDataMods(modsToKeep);
	}

	private static boolean modpackContains(Modpack modpack, InstallerData.ModData data) {
		for(ModInfo mod : modpack.getMods()) {
			if(mod.projectID == data.projectID && mod.fileID == data.fileID) {
				return true;
			}
		}
		return false;
	}

	private static void iterateModSources(InstallerConfig config, InstallerData data,
			Modpack modpack) {
		//TODO copy files, then modpack.removeMods

	}

	private static void copyNewFiles(Path modpackLocation, InstallerConfig config,
			InstallerData data, Modpack modpack)
			throws IOException {
		final Path overrides = modpackLocation.resolve(modpack.getOverrides());
		final Path installTo = Paths.get(config.installTo);
		final List<String> filesToIgnore =
				config.isServer ? modpack.getClientOnlyFiles() : modpack.getServerOnlyFiles();

		Files.walkFileTree(overrides, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
					throws IOException {
				copyFile(overrides, installTo, filesToIgnore, config, data, modpack, file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path directory,
					BasicFileAttributes attributes) throws IOException {
				visitDirectory(overrides, installTo, filesToIgnore, directory);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	static void copyFile(Path overrides, Path installTo, List<String> filesToIgnore,
			InstallerConfig config, InstallerData data, Modpack modpack, Path file)
			throws IOException {
		final Path relativized = relativize(overrides, file);

		if(shouldSkip(filesToIgnore, relativized)) {
			return;
		}

		final Path newFile = installTo(config, relativized);

		if(Files.isDirectory(newFile)) {
			NIOUtils.deleteDirectory(newFile);
		}

		final String name = name(file);

		try {
			CurseEventHandling.forEach(handler -> handler.copying(toString(relativized)));
		} catch(CurseException ex) {
			//It's just event handling, shouldn't matter too much ATM
		}

		boolean variablesReplaced = shouldReplaceVariables(config.variableFileExtensions, name);
		if(variablesReplaced) {
			variablesReplaced = replaceVariablesAndCopy(file, newFile, modpack);
		}

		if(!variablesReplaced) {
			Files.copy(file, newFile, StandardCopyOption.REPLACE_EXISTING);
		}

		if(!variablesReplaced) {
			if(config.shouldKeepModpack) {
				Files.copy(file, newFile, StandardCopyOption.REPLACE_EXISTING);
			} else {
				Files.move(file, newFile, StandardCopyOption.REPLACE_EXISTING);
			}
		}

		data.installedFiles.add(toString(relativized));
	}

	private static boolean shouldReplaceVariables(String[] variableFileExtensions, String name) {
		for(String extension : variableFileExtensions) {
			if(name.endsWith("." + extension)) {
				return true;
			}
		}
		return false;
	}

	static void visitDirectory(Path overrides, Path installTo, List<String> filesToIgnore,
			Path directory) throws IOException {
		directory = relativize(overrides, directory);

		if(shouldSkip(filesToIgnore, directory)) {
			return;
		}

		final Path installToPath = installTo.resolve(directory.toString());

		//Make sure installToPath is a directory that exists

		if(Files.exists(installToPath) && !Files.isDirectory(installToPath)) {
			Files.delete(installToPath);
		}

		if(!Files.exists(installToPath)) {
			Files.createDirectory(installToPath);
		}
	}

	private static boolean replaceVariablesAndCopy(Path file, Path newFile, Modpack modpack)
			throws IOException {
		try {
			final String toWrite = NIOUtils.readFile(file).
					replaceAll(MINECRAFT_VERSION, modpack.getMinecraftVersionString()).
					replaceAll(MODPACK_NAME, modpack.getName()).
					replaceAll(MODPACK_VERSION, modpack.getVersion()).
					replaceAll(FULL_MODPACK_NAME, modpack.getFullName()).
					replaceAll(MODPACK_AUTHOR, modpack.getAuthor()) +
					System.lineSeparator();

			NIOUtils.write(newFile, toWrite);
		} catch(MalformedInputException ex) {
			ex.printStackTrace();
			getLogger().error("This exception was caused by the file: " + file);
			getLogger().error("Make sure the file is encoded in UTF-8!");
			getLogger().error("Variables in this file will not be processed.");

			return false;
		}

		return true;
	}

	private static Path relativize(Path overrides, Path path) {
		return overrides.relativize(path).normalize();
	}

	private static boolean shouldSkip(List<String> filesToIgnore, Path path) {
		for(String fileName : filesToIgnore) {
			final Path toIgnore = Paths.get("config", fileName).normalize();
			if(path.equals(toIgnore) || NIOUtils.isParent(toIgnore, path)) {
				return true;
			}
		}
		return false;
	}

	private static void downloadMods(InstallerConfig config, InstallerData data, Modpack modpack)
			throws CurseException, IOException {
		if(modpack.getMods().isEmpty()) {
			return;
		}

		final AtomicInteger count = new AtomicInteger();

		final int threads = config.threads > 0 ? config.threads : CurseAPI.getMaximumThreads();

		final int size = modpack.getMods().size();
		try {
			ThreadUtils.splitWorkload(threads, size, index ->
					downloadMod(config, data, modpack.getMods().get(index),
							count.incrementAndGet(), size));
		} catch(Exception ex) {
			if(ex instanceof CurseException) {
				throw (CurseException) ex;
			}

			if(ex instanceof IOException) {
				throw (IOException) ex;
			}

			throw (RuntimeException) ex;
		}
	}

	private static void downloadMod(InstallerConfig config, InstallerData data,
			ModInfo mod, int count, int total) throws CurseException, IOException {
		CurseEventHandling.forEach(handler -> handler.downloadingMod(mod.title, count, total));

		final URL url = CurseForge.getFileURL(mod.projectID, mod.fileID);

		final Path downloaded = NIOUtils.downloadToDirectory(url, installTo(config, "mods"));

		final Path relativizedLocation = Paths.get(config.installTo).relativize(downloaded);

		final ModData modData = new ModData();

		modData.projectID = mod.projectID;
		modData.fileID = mod.fileID;
		modData.location = toString(relativizedLocation);
		modData.relatedFiles = mod.relatedFiles;

		CurseEventHandling.forEach(
				handler -> handler.downloadedMod(mod.title, name(downloaded), count));

		data.mods.add(modData);
	}

	private static void installForge(InstallerConfig config, InstallerData data, Modpack modpack) {
		data.minecraftVersion = modpack.getMinecraftVersion().toString();
		data.forgeVersion = modpack.getForgeVersion();

		if(!config.shouldInstallForge) {
			return;
		}

		//TODO
	}

	private static void createEULAAndServerStarters(InstallerConfig config, InstallerData data,
			Modpack modpack) {
		if(!config.isServer) {
			return;
		}

		//TODO
	}

	private static String name(Path path) {
		//Mainly to make SpotBugs happy
		final Path name = path.getFileName();
		return name == null ? "" : name.toString();
	}

	private static Path installTo(InstallerConfig config, Path path) {
		return installTo(config, path.toString());
	}

	private static Path installTo(InstallerConfig config, String... paths) {
		return Paths.get(config.installTo, paths);
	}

	private static String toString(Path path) {
		return NIOUtils.toStringWithUnixPathSeparators(path);
	}

	private static void deleteTemporaryFiles() {
		for(int i = 0; i < temporaryFiles.size(); i++) {
			try {
				if(Files.isDirectory(temporaryFiles.get(i))) {
					NIOUtils.deleteDirectoryIfExists(temporaryFiles.get(i));
				} else {
					Files.deleteIfExists(temporaryFiles.get(i));
				}
				temporaryFiles.remove(i--);
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
