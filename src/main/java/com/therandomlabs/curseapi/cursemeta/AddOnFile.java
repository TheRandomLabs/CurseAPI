package com.therandomlabs.curseapi.cursemeta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.FileStatus;
import com.therandomlabs.curseapi.game.Game;
import com.therandomlabs.curseapi.util.Utils;
import com.therandomlabs.utils.collection.TRLList;

public class AddOnFile implements Cloneable {
	public int Id;
	public int AlternateFileId;
	public List<AddOnFileDependency> Dependencies;
	public String DownloadURL;
	public String FileDate;
	public String FileName;
	public String FileNameOnDisk;
	public FileStatus FileStatus;
	public String[] GameVersion;
	public boolean IsAlternate;
	public boolean IsAvailable;
	public long PackageFingerprint;
	public String ReleaseType;
	public List<AddOnModule> Modules;

	@Override
	public AddOnFile clone() {
		try {
			final AddOnFile file = (AddOnFile) super.clone();

			file.Dependencies = Utils.tryClone(Dependencies);
			file.Modules = Utils.tryClone(Modules);

			return file;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	public com.therandomlabs.curseapi.file.ReleaseType releaseType() {
		return com.therandomlabs.curseapi.file.ReleaseType.fromName(ReleaseType);
	}

	public static TRLList<CurseFile> toCurseFiles(Map<Integer, Collection<AddOnFile>> files,
			Game game) throws CurseException {
		final TRLList<CurseFile> curseFiles = new TRLList<>(files.size());

		for(Map.Entry<Integer, Collection<AddOnFile>> entry : files.entrySet()) {
			for(AddOnFile file : entry.getValue()) {
				curseFiles.add(new CurseFile(entry.getKey(), game, file));
			}
		}

		return curseFiles;
	}
}
