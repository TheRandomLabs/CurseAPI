package com.therandomlabs.curseapi.minecraft.modpack;

import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseFile;
import com.therandomlabs.curseapi.CurseFileList;
import com.therandomlabs.curseapi.CurseProject;
import com.therandomlabs.curseapi.util.CloneException;

public final class ModpackFileInfo implements Cloneable {
	public static final String UNKNOWN_TITLE = "Unknown Name";

	public String title = UNKNOWN_TITLE;
	public int projectID;
	public int fileID;
	public FileType type = FileType.NORMAL;
	public String[] relatedFiles = new String[0];
	public AlternativeFileInfo[] alternatives = new AlternativeFileInfo[0];

	public ModpackFileInfo() {}

	public ModpackFileInfo(String title, int projectID, int fileID, FileType type,
			String[] relatedFiles, AlternativeFileInfo[] alternatives) {
		this.title = title;
		this.projectID = projectID;
		this.fileID = fileID;
		this.type = type;
		this.relatedFiles = relatedFiles;
		this.alternatives = alternatives;
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof ModpackFileInfo) {
			return ((ModpackFileInfo) object).fileID == fileID;
		}

		if(object instanceof InstallerData.ModData) {
			return ((InstallerData.ModData) object).fileID == fileID;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return fileID;
	}

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

	public AlternativeFileInfo toAlternative() {
		final AlternativeFileInfo info = new AlternativeFileInfo();

		info.title = title;
		info.projectID = projectID;
		info.fileID = fileID;
		info.type = type;
		info.relatedFiles = relatedFiles.clone();

		return info;
	}

	public static CurseFile[] toCurseFiles(ModpackFileInfo[] files) throws CurseException {
		final CurseFile[] curseFiles = new CurseFile[files.length];

		for(int i = 0; i < files.length; i++) {
			curseFiles[i] = CurseProject.fromID(files[i].projectID).fileFromID(files[i].fileID);
		}

		return curseFiles;
	}

	public static CurseFileList toCurseFileList(ModpackFileInfo[] files) throws CurseException {
		return CurseFileList.of(toCurseFiles(files));
	}
}
