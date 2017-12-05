package com.therandomlabs.curseapi.minecraft.modpack;

import java.util.List;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseFile;
import com.therandomlabs.curseapi.CurseFileList;
import com.therandomlabs.curseapi.CurseProject;
import com.therandomlabs.curseapi.util.CloneException;
import com.therandomlabs.utils.collection.ImmutableList;

public final class ModpackFileInfo implements Cloneable {
	public String title = "Unknown Title";
	public int projectID;
	public int fileID;
	public FileType type = FileType.NORMAL;
	public String[] relatedFiles = new String[0];
	public AlternativeFileInfo[] alternatives = new AlternativeFileInfo[0];

	@Override
	public ModpackFileInfo clone() {
		final ModpackFileInfo info = new ModpackFileInfo();

		info.title = title;
		info.projectID = projectID;
		info.fileID = fileID;
		info.type = type;
		info.relatedFiles = relatedFiles.clone();
		info.alternatives = CloneException.tryClone(alternatives);

		return info;
	}

	public static ModpackFileInfo[] fromCurseFiles(CurseFile[] files) {
		return fromCurseFiles(new ImmutableList<>(files));
	}

	public static ModpackFileInfo[] fromCurseFiles(List<? extends CurseFile> files) {
		final ModpackFileInfo[] infos = new ModpackFileInfo[files.size()];

		for(int i = 0; i < infos.length; i++) {
			final CurseFile file = files.get(i);
			final ModpackFileInfo info = new ModpackFileInfo();

			info.title = file.project().title();
			info.projectID = file.project().id();
			info.fileID = file.id();

			if(file instanceof ModpackFile) {
				final ModpackFile modpackFile = (ModpackFile) file;
				info.type = modpackFile.getType();
				info.relatedFiles = modpackFile.getRelatedFiles().toArray(new String[0]);
				info.alternatives = AlternativeFileInfo.fromArray(
						fromCurseFiles(modpackFile.getAlternatives()));
			}

			infos[i] = info;
		}

		return infos;
	}

	public static CurseFile[] toCurseFiles(ModpackFileInfo[] files) throws CurseException {
		final CurseFile[] curseFiles = new CurseFile[files.length];

		for(int i = 0; i < files.length; i++) {
			curseFiles[i] = new ModpackFile(
					CurseProject.fromID(files[i].projectID).fileFromID(files[i].fileID),
					files[i].type, files[i].relatedFiles, toCurseFiles(files[i].alternatives));
		}

		return curseFiles;
	}

	private static CurseFile[] toCurseFiles(AlternativeFileInfo[] files) throws CurseException {
		final CurseFile[] curseFiles = new CurseFile[files.length];

		for(int i = 0; i < files.length; i++) {
			curseFiles[i] = new ModpackFile(
					CurseProject.fromID(files[i].projectID).fileFromID(files[i].fileID),
					files[i].type, files[i].relatedFiles, new CurseFile[0]);
		}

		return curseFiles;
	}

	public static CurseFileList toCurseFileList(ModpackFileInfo[] files) throws CurseException {
		return CurseFileList.of(toCurseFiles(files));
	}
}
