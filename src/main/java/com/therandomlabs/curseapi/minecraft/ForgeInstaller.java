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

	public static ForgeInstaller downloadLatestToDirectory(MinecraftVersion version,
			Path directory) throws CurseException, IOException {
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

	/*final Process process = new ProcessBuilder(Paths.get(
			SystemProperties.JAVA_INSTALLATION_DIRECTORY.get(),
			"bin",
			"java.exe"
		).toString(),
			//The Forge installer can have issues with downloading libraries if
			//preferIPv4Stack is not set to true
			"-Djava.net.preferIPv4Stack=true",
			"-jar",
			installer.toString(),
			"--installServer").
			directory(new File(config.workingDirectory)).redirectErrorStream(true).start();

		//Capture output
		final BufferedReader reader =
			new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";
		boolean failedToDownload = false;
		while((line = reader.readLine()) != null) {
		if(line.contains("These libraries failed to download")) {
			failedToDownload = true;
		}
		progressUpdater.installingForge(line, failedToDownload ||
				line.startsWith("java.io.IOException") || line.startsWith("\t"));
		}
		reader.close();

		return failedToDownload;
		//Installing Forge

		progressUpdater.installingForge(data.forgeVersion);

		filesToDelete.add(installer);
		if(!config.saveForgeInstallerLog) {
		filesToDelete.add(file("installer.log"));
		filesToDelete.add(file(installer.getFileName() + ".log"));
		}

		if(installForge(installer)) {
		final Scanner scanner = new Scanner(System.in);
		while(true) {
			System.out.print("[INFO] Forge installation failed. try again? (Y/N) ");
			final String input = scanner.next();
			if(input.equalsIgnoreCase("Y")) {
				if(!installForge(installer)) {
					break;
				}
			} else if(input.equalsIgnoreCase("N")) {
				break;
			} else {
				getLogger().error("Invalid input.");
			}
		}
		scanner.close();
		}

		//Rename Forge Universal to the modpack name
		if(manifest != null && manifest.name != null && !manifest.name.isEmpty()) {
		Files.move(
				file(FORGE_UNIVERSAL_FILE_NAME.replace("VERSION", data.forgeVersion)),
				file(manifest.name + ".jar"),
				StandardCopyOption.REPLACE_EXISTING
		);
		}

		return manifest == null ? FORGE_UNIVERSAL_FILE_NAME.replace("VERSION", data.forgeVersion) :
		manifest.name;
	}*/
}
