package com.therandomlabs.curseapi.minecraft.modpack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseFile;
import com.therandomlabs.curseapi.CurseFileList;
import com.therandomlabs.curseapi.CurseProject;
import com.therandomlabs.curseapi.ReleaseType;
import com.therandomlabs.curseapi.minecraft.MinecraftForge;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.util.ThreadWithIndexValues;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.number.NumberUtils;
import com.therandomlabs.utils.wrapper.Wrapper;

//CurseAPI Manifest Format
public final class CAFormat {
	public static final String IMPORT = "import";

	public static final String NAME = "name";
	public static final String DEFAULT_NAME = "CurseAPI Modpack";

	public static final String MINECRAFT = "minecraft";
	public static final String DEFAULT_MINECRAFT = MinecraftVersion.latest().toString();

	public static final String VERSION = "version";
	public static final String DEFAULT_VERSION = DEFAULT_MINECRAFT + "-1.0.0.0";

	public static final String FORGE_RECOMMENDED = "recommended";
	public static final String FORGE_LATEST = "latest";

	public static final String FORGE = "forge";
	public static final String DEFAULT_FORGE = FORGE_LATEST;

	public static final String MINIMUM_STABILITY = "minimum_stability";
	public static final String DEFAULT_MINIMUM_STABILITY = ReleaseType.ALPHA.toString();

	public static final String AUTHOR = "author";
	public static final String DEFAULT_AUTHOR = "CurseAPI";

	public static final String DESCRIPTION = "description";
	public static final String DEFAULT_DESCRIPTION = "No description provided.";

	public static final String VARIABLE = "#";
	public static final String CLIENT_ONLY = "!c";
	public static final String SERVER_ONLY = "!s";
	public static final String COMMENT = ":";
	public static final String ALTERNATIVE = "|";
	public static final String NEWER_THAN_OR_EQUAL_TO = ">";
	public static final String OLDER_THAN_OR_EQUAL_TO = "<";

	private CAFormat() {}

	private static class FileData {
		final FileType type;
		final int projectID;
		final int fileID;
		final List<String> relatedFiles;
		List<FileData> alternatives;

		FileData(FileType type, int projectID, int fileID, List<String> relatedFiles) {
			this.type = type;
			this.projectID = projectID;
			this.fileID = fileID;
			this.relatedFiles = relatedFiles;
		}
	}

	public static void writeCurseManifest(File manifest, File output)
			throws CurseException, IOException {
		writeCurseManifest(manifest.toPath(), output.toPath());
	}

	public static void writeCurseManifest(Path manifest, Path output)
			throws CurseException, IOException {
		Files.write(output, (toCurseManifest(manifest) + System.lineSeparator()).getBytes());
	}

	public static String toCurseManifest(File manifest) throws CurseException, IOException {
		return toCurseManifest(manifest.toPath());
	}

	public static String toCurseManifest(Path manifest) throws CurseException, IOException {
		return toModpack(manifest).toPrettyJsonWithTabs();
	}

	public static ModpackInfo toModpackInfo(File manifest) throws CurseException, IOException {
		return toModpackInfo(manifest.toPath());
	}

	public static ModpackInfo toModpackInfo(Path manifest) throws CurseException, IOException {
		return toModpack(manifest).toModpackInfo();
	}

	public static Modpack toModpack(File manifest) throws CurseException, IOException {
		return toModpack(manifest.toPath());
	}

	public static Modpack toModpack(Path manifest) throws CurseException, IOException {
		final Map<String, String> variables = new HashMap<>();
		final List<FileData> files = new ArrayList<>();

		variables.put(NAME, DEFAULT_NAME);
		variables.put(MINECRAFT, DEFAULT_MINECRAFT);
		variables.put(VERSION, DEFAULT_VERSION);
		variables.put(FORGE, DEFAULT_FORGE);
		variables.put(MINIMUM_STABILITY, DEFAULT_MINIMUM_STABILITY);
		variables.put(AUTHOR, DEFAULT_AUTHOR);
		variables.put(DESCRIPTION, DEFAULT_DESCRIPTION);

		final List<String> lines = Files.readAllLines(manifest);

		for(int i = 0; i < lines.size(); i++) {
			final String line = lines.get(i).trim();
			if(line.isEmpty()) {
				continue;
			}

			String[] data = StringUtils.splitWhitespace(line);

			if(data[0].startsWith(NEWER_THAN_OR_EQUAL_TO)) {
				if(data.length < 2) {
					continue;
				}

				final String versionToCompare = data[0].substring(NEWER_THAN_OR_EQUAL_TO.length());
				if(MinecraftVersion.fromString(versionToCompare).
						compareTo(MinecraftVersion.fromString(variables.get(MINECRAFT))) >= 0) {
					data = ArrayUtils.subArray(data, 1);
				} else {
					continue;
				}
			}

			if(data[0].startsWith(OLDER_THAN_OR_EQUAL_TO)) {
				if(data.length < 2) {
					continue;
				}

				final String versionToCompare = data[0].substring(OLDER_THAN_OR_EQUAL_TO.length());
				if(MinecraftVersion.fromString(versionToCompare).
						compareTo(MinecraftVersion.fromString(variables.get(MINECRAFT))) <= 0) {
					data = ArrayUtils.subArray(data, 1);
				} else {
					continue;
				}
			}

			//Variables

			if(data[0].equals(VARIABLE)) {
				if(data.length > 2) {
					final String lowerCase = data[1].toLowerCase(Locale.ENGLISH);
					variables.put(lowerCase, ArrayUtils.join(ArrayUtils.subArray(data, 2), " "));

					//Imports
					if(lowerCase.equals(IMPORT)) {
						final Path path = Paths.get(variables.get(IMPORT));
						if(Files.exists(path)) {
							//Do not import variables
							Files.readAllLines(path).stream().
									filter(string -> !string.startsWith(VARIABLE)).
									forEach(lines::add);
						}
					}
				}
				continue;
			}

			//FileData

			final List<FileData> alternatives = new ArrayList<>();
			final FileData file = getFile(data, variables, alternatives);
			if(file != null) {
				file.alternatives = alternatives;
				files.add(file);
			}
		}

		return new Modpack(
				variables.get(NAME),
				variables.get(VERSION),
				variables.get(AUTHOR),
				variables.get(DESCRIPTION),
				MinecraftVersion.fromString(variables.get(MINECRAFT)),
				getForge(variables.get(MINECRAFT), variables.get(FORGE)),
				toCurseFileList(variables, files)
		);
	}

	private static CurseFileList toCurseFileList(Map<String, String> variables,
			List<FileData> fileData) throws CurseException {
		final List<ModpackFile> files = new ArrayList<>(fileData.size());

		final int threadCount = fileData.size() < CurseAPI.getMaximumThreads() ?
				fileData.size() : CurseAPI.getMaximumThreads();
		final Thread[] threads = new Thread[threadCount];
		final int increment = fileData.size() / threadCount;

		final Wrapper<CurseException> exception = new Wrapper<>();

		for(int i = 0, j = 0; i < threadCount; i++, j += increment) {
			threads[i] = new ThreadWithIndexValues(i, j, indexes -> {
				final int threadIndex = indexes.value1();
				final int startIndex = indexes.value2();

				final int endIndex;
				if(threadIndex == threadCount - 1) {
					endIndex = fileData.size();
				} else {
					endIndex = startIndex + increment;
				}

				try {
					for(int fileIndex = startIndex; fileIndex < endIndex; fileIndex++) {
						final ModpackFile file = toModpackFile(variables, fileData.get(fileIndex));
						if(file != null) {
							files.add(file);
						}
					}
				} catch(CurseException ex) {
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
			throw exception.get();
		}

		return CurseFileList.ofUnsorted(files).sortedByProjectTitle().filterDuplicateProjects();
	}

	private static ModpackFile toModpackFile(Map<String, String> variables, FileData data)
			throws CurseException {
		final CurseProject project = CurseProject.fromID(data.projectID);

		final CurseFile file;
		if(data.fileID == 0) {
			final CurseFileList list = project.files().
					filterMCVersionGroup(variables.get(MINECRAFT)).
					filterMinimumStability(
					ReleaseType.fromName(variables.get(MINIMUM_STABILITY)));
			if(list.isEmpty()) {
				return null;
			}

			file = list.get(0);
		} else {
			file = project.fileFromID(data.fileID);
		}

		final List<CurseFile> alternatives;
		if(data.alternatives != null) {
			alternatives = new ArrayList<>(data.alternatives.size());
			for(FileData alternative : data.alternatives) {
				alternatives.add(toModpackFile(variables, alternative));
			}
		} else {
			alternatives = ImmutableList.empty();
		}

		return new ModpackFile(file, data.type, data.relatedFiles, alternatives);
	}

	private static String getForge(String minecraft, String forge) throws CurseException {
		forge = forge.toLowerCase(Locale.ENGLISH);

		if(forge.equals(FORGE_LATEST)) {
			return MinecraftForge.getLatestVersion(minecraft);
		}

		if(forge.equals(FORGE_RECOMMENDED)) {
			return MinecraftForge.getRecommendedVersion(minecraft);
		}

		return forge;
	}

	private static FileData getFile(String[] data, Map<String, String> variables,
			List<FileData> alternatives)
			throws CurseException {
		if(data.length == 0) {
			return null;
		}

		//Check for CAFormat.COMMENT and CAFormat.ALTERNATIVE

		int dataIndex;
		for(dataIndex = 0; dataIndex < data.length; dataIndex++) {
			if(data[dataIndex].startsWith(COMMENT)) {
				break;
			}

			if(data[dataIndex].equals(ALTERNATIVE)) {
				if(dataIndex != data.length - 1) {
					final FileData file = getFile(
							ArrayUtils.subArray(data, dataIndex + 1), variables, alternatives);
					if(file != null) {
						alternatives.add(file);
					}
				}

				break;
			}
		}

		data = ArrayUtils.subArray(data, 0, dataIndex);

		//Get type

		final String lowerCase = data[0].toLowerCase(Locale.ENGLISH);
		final boolean clientOnly = lowerCase.equals(CLIENT_ONLY);
		final boolean serverOnly = lowerCase.equals(SERVER_ONLY);

		if(clientOnly || serverOnly) {
			if(data.length == 1) {
				return null;
			}

			data = ArrayUtils.subArray(data, 1);
		}

		final FileType type = FileType.fromBooleans(clientOnly, serverOnly);

		//Get project ID, then the CurseProject

		final int projectID = NumberUtils.parseInt(data[0], 0);
		if(projectID < CurseAPI.MIN_PROJECT_ID) {
			return null;
		}

		//Get file ID

		int relatedFilesStartIndex = 1;

		int fileID = data.length > 1 ? NumberUtils.parseInt(data[1], 0) : 0;
		if(fileID != 0) {
			//A file was specified, so the related files would be at another index
			relatedFilesStartIndex++;
		}

		//Get related files

		final List<String> relatedFiles = new ArrayList<>();

		if(data.length > relatedFilesStartIndex) {
			for(int i = relatedFilesStartIndex; i < data.length; i++) {
				relatedFiles.add(data[i]);
			}
		}

		return new FileData(type, projectID, fileID, relatedFiles);
	}
}
