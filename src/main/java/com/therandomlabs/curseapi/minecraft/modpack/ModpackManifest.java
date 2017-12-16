package com.therandomlabs.curseapi.minecraft.modpack;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseFile;
import com.therandomlabs.curseapi.CurseFileList;
import com.therandomlabs.curseapi.CurseProject;
import com.therandomlabs.curseapi.util.CloneException;
import com.therandomlabs.utils.collection.TRLList;

public final class ModpackManifest implements Cloneable {
	public String manifestType;
	public int manifestVersion;
	public String name;
	public String version;
	public String author;
	public String description;
	public ModInfo[] files;
	public String overrides;
	public MinecraftInfo minecraft;
	public String optifineVersion = "";
	public double minimumRam = 2.5;
	public double recommendedRam = 4.0;

	public static class UpdateInfo implements Cloneable, Serializable {
		private static final long serialVersionUID = 7002390917108852490L;

		private CurseProject project;

		private final ModInfo oldMod;
		private CurseFile oldModFile;

		private final ModInfo newMod;
		private CurseFile newModFile;

		UpdateInfo(ModInfo oldMod, ModInfo newMod) {
			this.oldMod = oldMod;
			this.newMod = newMod;
		}

		public CurseProject getProject() throws CurseException {
			if(project == null) {
				project = CurseProject.fromID(newMod.projectID);
			}
			return project;
		}

		public ModInfo getOldMod() {
			return oldMod;
		}

		public CurseFile getOldModFile() throws CurseException {
			if(oldModFile == null) {
				oldModFile = getProject().fileFromID(oldMod.fileID);
			}
			return oldModFile;
		}

		public String getOldModName() throws CurseException {
			return getOldModFile().name();
		}

		public ModInfo getNewMod() {
			return newMod;
		}

		public CurseFile getNewModFile() throws CurseException {
			if(newModFile == null) {
				newModFile = getProject().fileFromID(newMod.fileID);
			}
			return newModFile;
		}

		public String getNewModName() throws CurseException {
			return getNewModFile().name();
		}

		public Map<String, String> getChangelog() throws CurseException {
			if(isDowngrade()) {
				return Collections.emptyMap();
			}

			final Map<String, String> changelog = new HashMap<>();

			final CurseFileList files = getProject().files().
					between(getOldModFile(), getNewModFile());

			for(CurseFile file : files) {
				changelog.put(file.name(), file.changelog());
			}

			return changelog;
		}

		public boolean isDowngrade() {
			return oldMod.fileID > newMod.fileID;
		}

		@Override
		public UpdateInfo clone() {
			return new UpdateInfo(oldMod.clone(), newMod.clone());
		}
	}

	public static class Changelog implements Serializable {
		private static final long serialVersionUID = 2442877055944479055L;

		private final ModpackManifest oldManifest;
		private final ModpackManifest newManifest;

		private final TRLList<ModInfo> unchanged;
		private final TRLList<UpdateInfo> updated;
		private final TRLList<UpdateInfo> downgraded;
		private final TRLList<ModInfo> removed;
		private final TRLList<ModInfo> added;

		Changelog(ModpackManifest oldManifest, ModpackManifest newManifest) {
			this.oldManifest = oldManifest;
			this.newManifest = newManifest;

			final TRLList<ModInfo> unchanged = new TRLList<>();
			final TRLList<UpdateInfo> updated = new TRLList<>();
			final TRLList<UpdateInfo> downgraded = new TRLList<>();
			final TRLList<ModInfo> removed = new TRLList<>();
			final TRLList<ModInfo> added = new TRLList<>();

			for(ModInfo oldMod : oldManifest.files) {
				boolean found = false;

				for(ModInfo newMod : newManifest.files) {
					if(oldMod.projectID == newMod.projectID) {
						found = true;

						if(oldMod.fileID == newMod.fileID) {
							unchanged.add(newMod);
							break;
						}

						if(newMod.fileID > oldMod.fileID) {
							updated.add(new UpdateInfo(oldMod, newMod));
							break;
						}

						downgraded.add(new UpdateInfo(newMod, oldMod));
						break;
					}
				}

				if(!found) {
					removed.add(oldMod);
				}
			}

			for(ModInfo newMod : newManifest.files) {
				boolean found = false;

				for(ModInfo oldMod : oldManifest.files) {
					if(oldMod.projectID == newMod.projectID) {
						found = true;
						break;
					}
				}

				if(!found) {
					added.add(newMod);
				}
			}

			this.unchanged = unchanged.toImmutableList();
			this.updated = updated.toImmutableList();
			this.downgraded = downgraded.toImmutableList();
			this.removed = removed.toImmutableList();
			this.added = added.toImmutableList();
		}

		public ModpackManifest getOldManifest() {
			return oldManifest;
		}

		public ModpackManifest getNewManifest() {
			return newManifest;
		}

		public TRLList<ModInfo> getUnchanged() {
			return unchanged;
		}

		public TRLList<UpdateInfo> getUpdated() {
			return updated;
		}

		public TRLList<UpdateInfo> getDowngraded() {
			return downgraded;
		}

		public TRLList<ModInfo> getRemoved() {
			return removed;
		}

		public TRLList<ModInfo> getAdded() {
			return added;
		}
	}

	@Override
	public ModpackManifest clone() {
		final ModpackManifest info = new ModpackManifest();

		info.manifestType = manifestType;
		info.manifestVersion = manifestVersion;
		info.name = name;
		info.version = version;
		info.author = author;
		info.description = description;
		info.files = CloneException.tryClone(files);
		info.overrides = overrides;
		info.minecraft = minecraft.clone();

		return info;
	}

	public Modpack toModpack() throws CurseException {
		return new Modpack(name, version, author, description, overrides, minecraft.version,
				minecraft.modLoaders[0].id.substring(6), files, optifineVersion, minimumRam,
				recommendedRam);
	}

	public static Changelog changelog(ModpackManifest oldManifest, ModpackManifest newManifest) {
		return new Changelog(oldManifest, newManifest);
	}
}
