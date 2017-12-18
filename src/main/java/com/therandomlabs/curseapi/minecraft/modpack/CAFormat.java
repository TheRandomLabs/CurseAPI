package com.therandomlabs.curseapi.minecraft.modpack;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseFile;
import com.therandomlabs.curseapi.CurseFileList;
import com.therandomlabs.curseapi.CurseProject;
import com.therandomlabs.curseapi.ReleaseType;
import com.therandomlabs.curseapi.minecraft.MinecraftForge;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.concurrent.ThreadUtils;
import com.therandomlabs.utils.io.NIOUtils;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.network.NetworkUtils;
import com.therandomlabs.utils.number.NumberUtils;

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

	public static final String OPTIFINE = "optifine";
	public static final String DEFAULT_OPTIFINE = "latest";

	public static final String MINIMUM_RAM = "minimum_ram";
	public static final String DEFAULT_MINIMUM_RAM = "2.5";

	public static final String RECOMMENDED_RAM = "recommended_ram";
	public static final String DEFAULT_RECOMMENDED_RAM = "4";

	public static final char VARIABLE = '#';
	public static final String CLIENT_ONLY = "!c";
	public static final String SERVER_ONLY = "!s";
	public static final char COMMENT = ':';
	public static final char ALTERNATIVE = '|';
	public static final char NEWER_THAN_OR_EQUAL_TO = '>';
	public static final char OLDER_THAN_OR_EQUAL_TO = '<';
	public static final char EQUAL_TO = '=';
	public static final char REMOVE_PROJECT_ID = '-';

	private CAFormat() {}

	private static class FileData {
		final FileType type;
		final int projectID;
		final int fileID;
		final String[] relatedFiles;
		FileData[] alternatives;

		FileData(FileType type, int projectID, int fileID, String[] relatedFiles) {
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
		NIOUtils.write(output, toCurseManifest(manifest), true);
	}

	public static String toCurseManifest(File manifest) throws CurseException, IOException {
		return toCurseManifest(manifest.toPath());
	}

	public static String toCurseManifest(Path manifest) throws CurseException, IOException {
		return toModpack(manifest).toPrettyJsonWithTabs();
	}

	public static ModpackManifest toModpackInfo(File manifest) throws CurseException, IOException {
		return toModpackInfo(manifest.toPath());
	}

	public static ModpackManifest toModpackInfo(Path manifest) throws CurseException, IOException {
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
		variables.put(OPTIFINE, DEFAULT_OPTIFINE);
		variables.put(MINIMUM_RAM, DEFAULT_MINIMUM_RAM);
		variables.put(RECOMMENDED_RAM, DEFAULT_RECOMMENDED_RAM);

		final List<String> lines = Files.readAllLines(manifest);

		for(int i = 0; i < lines.size(); i++) {
			final String line = lines.get(i).trim();
			if(line.isEmpty()) {
				continue;
			}

			String[] data = StringUtils.splitWhitespace(line);
			char operator = data[0].charAt(0);

			if(operator == NEWER_THAN_OR_EQUAL_TO || operator == OLDER_THAN_OR_EQUAL_TO ||
					operator == EQUAL_TO) {
				if(data.length < 2) {
					continue;
				}

				final MinecraftVersion versionToCompare =
						MinecraftVersion.fromString(data[0].substring(1));
				final MinecraftVersion version =
						MinecraftVersion.fromString(variables.get(MINECRAFT));
				final int compare = versionToCompare.compareTo(version);

				boolean matches = false;

				switch(operator) {
				case NEWER_THAN_OR_EQUAL_TO:
					matches = compare >= 0;
					break;
				case OLDER_THAN_OR_EQUAL_TO:
					matches = compare <= 0;
					break;
				case EQUAL_TO:
					matches = compare == 0;
				}

				if(!matches) {
					continue;
				}

				data = ArrayUtils.subArray(data, 1);
			}

			if(data[0].equals(String.valueOf(REMOVE_PROJECT_ID))) {
				final int id = NumberUtils.parseInt(data[0].substring(1), 0);
				if(id >= CurseAPI.MIN_PROJECT_ID) {
					for(int j = 0; j < files.size(); j++) {
						if(files.get(j).projectID == id) {
							files.remove(j--);
						}
					}
				}
			}

			if(data[0].equals(String.valueOf(VARIABLE))) {
				if(data.length > 2) {
					final String lowerCase = data[1].toLowerCase(Locale.ENGLISH);
					variables.put(lowerCase, ArrayUtils.join(ArrayUtils.subArray(data, 2), " "));

					//Imports
					if(lowerCase.equals(IMPORT)) {
						final String importLocation = variables.get(IMPORT);
						List<String> toImport = null;

						try {
							final String string = NetworkUtils.read(new URL(importLocation));
							toImport =
									new ImmutableList<>(StringUtils.NEWLINE_REGEX.split(string));
						} catch(MalformedURLException ex) {
							toImport = Files.readAllLines(Paths.get(importLocation));
						}

						if(toImport == null) {
							continue;
						}

						//Do not import variables
						lines.addAll(i + 1, toImport.stream().
								filter(string -> string.charAt(0) != VARIABLE).
								collect(Collectors.toList()));
					}
				}
				continue;
			}

			//FileData

			final List<FileData> alternatives = new ArrayList<>();
			final FileData file = getFile(data, variables, alternatives);
			if(file != null) {
				file.alternatives = alternatives.toArray(new FileData[0]);
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
				getFiles(variables, files),
				variables.get(OPTIFINE),
				Double.parseDouble(variables.get(MINIMUM_RAM)),
				Double.parseDouble(variables.get(RECOMMENDED_RAM))
		);
	}

	private static ModInfo[] getFiles(Map<String, String> variables,
			List<FileData> fileData) throws CurseException {
		final List<ModInfo> files = new ArrayList<>(fileData.size());

		ThreadUtils.splitWorkload(CurseAPI.getMaximumThreads(), fileData.size(), index -> {
			final ModInfo file = toModpackFile(variables, fileData.get(index));
			if(file != null) {
				files.add(file);
			}
		});

		files.sort((file1, file2) -> file1.title.compareTo(file2.title));

		//Remove duplicate projects

		final List<ModInfo> duplicates = new ArrayList<>();

		for(ModInfo file : files) {
			for(ModInfo file2 : files) {
				if(file != file2 && file.projectID == file2.projectID) {
					//Prefer the newer file
					if(file.fileID > file2.fileID) {
						duplicates.add(file2);
					} else {
						duplicates.add(file);
					}
				}
			}
		}

		files.removeAll(duplicates);

		return files.toArray(new ModInfo[0]);
	}

	private static ModInfo toModpackFile(Map<String, String> variables, FileData data)
			throws CurseException {
		final CurseProject project = CurseProject.fromID(data.projectID);

		final CurseFile file;
		if(data.fileID == 0) {
			final CurseFileList list = project.files().
					filterMCVersionGroup(variables.get(MINECRAFT)).
					filterMinimumStability(ReleaseType.fromName(variables.get(MINIMUM_STABILITY)));
			if(list.isEmpty()) {
				return null;
			}

			file = list.get(0);
		} else {
			file = project.fileFromID(data.fileID);
		}

		final List<AlternativeFileInfo> alternatives;
		if(data.alternatives != null) {
			alternatives = new ArrayList<>(data.alternatives.length);
			for(FileData alternative : data.alternatives) {
				alternatives.add(toModpackFile(variables, alternative).toAlternative());
			}
		} else {
			alternatives = ImmutableList.empty();
		}

		return new ModInfo(project.title(), data.projectID, file.id(), data.type,
				data.relatedFiles, alternatives.toArray(new AlternativeFileInfo[0]));
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
			if(data[dataIndex].charAt(0) == COMMENT) {
				break;
			}

			//TODO better alternative syntax
			//mods can declare dependencies as project IDs
			//dependencies, e.g. if mod A is chosen, remove dependency B
			//optional mods with dependencies
			if(data[dataIndex].equals(String.valueOf(ALTERNATIVE))) {
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

		return new FileData(type, projectID, fileID, relatedFiles.toArray(new String[0]));
	}
}
