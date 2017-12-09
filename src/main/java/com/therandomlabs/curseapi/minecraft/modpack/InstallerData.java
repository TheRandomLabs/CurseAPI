package com.therandomlabs.curseapi.minecraft.modpack;

import java.util.ArrayList;
import java.util.List;
import com.therandomlabs.curseapi.util.CloneException;

public final class InstallerData implements Cloneable {
	public static class ModData implements Cloneable {
		public int projectID;
		public int fileID;
		public String location;
		public String[] relatedFiles;

		@Override
		public boolean equals(Object object) {
			if(this == object) {
				return true;
			}

			if(object instanceof ModData) {
				return ((ModData) object).fileID == fileID;
			}

			if(object instanceof ModpackFileInfo) {
				return ((ModpackFileInfo) object).fileID == fileID;
			}

			return false;
		}

		@Override
		public int hashCode() {
			return fileID;
		}

		@Override
		public ModData clone() {
			final ModData data = new ModData();

			data.projectID = projectID;
			data.fileID = fileID;
			data.location = location;
			data.relatedFiles = relatedFiles;

			return data;
		}
	}

	public String minecraftVersion;
	public String forgeVersion;
	public List<ModData> mods = new ArrayList<>();
	public ArrayList<String> installedFiles = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@Override
	public InstallerData clone() {
		final InstallerData data = new InstallerData();

		data.minecraftVersion = minecraftVersion;
		data.forgeVersion = forgeVersion;
		data.mods = CloneException.tryClone(mods);
		data.installedFiles = (ArrayList<String>) installedFiles.clone();

		return data;
	}
}
