package com.therandomlabs.curseapi.cursemeta;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.FileStatus;
import com.therandomlabs.curseapi.file.ReleaseType;
import com.therandomlabs.curseapi.game.Game;
import com.therandomlabs.curseapi.util.Utils;
import com.therandomlabs.utils.collection.TRLList;

public final class CMFile implements Cloneable {
	public int id;
	public String fileName;
	public String fileNameOnDisk;
	public String fileDate;
	public int fileLength;
	public int releaseType;
	public int fileStatus;
	public URL downloadUrl;
	public boolean isAlternate;
	public int alternateFileId;
	public CMDependency[] dependencies;
	public boolean isAvailable;
	public CMModule[] modules;
	public long packageFingerprint;
	public String[] gameVersion;
	public String installMetadata;

	@Override
	public CMFile clone() {
		try {
			final CMFile file = (CMFile) super.clone();

			file.dependencies = Utils.tryClone(dependencies);
			file.modules = Utils.tryClone(modules);

			return file;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	public ReleaseType releaseType(){
		switch(releaseType) {
		case 1:
			return ReleaseType.RELEASE;
		case 2:
			return ReleaseType.BETA;
		case 3:
			return ReleaseType.ALPHA;
		default:
			return ReleaseType.UNKNOWN;
		}
	}

	public FileStatus fileStatus() {
		switch(fileStatus) {
		case 2:
			return FileStatus.NORMAL;
		case 3:
			return FileStatus.SEMI_NORMAL; //Guessing
		default:
			return FileStatus.UNKNOWN;
		}
	}

	public static TRLList<CurseFile> toCurseFiles(Map<Integer, Collection<CMFile>> files, Game game)
			throws CurseException {
		final TRLList<CurseFile> curseFiles = new TRLList<>(files.size());

		for(Map.Entry<Integer, Collection<CMFile>> entry : files.entrySet()) {
			for(CMFile file : entry.getValue()) {
				curseFiles.add(new CurseFile(entry.getKey(), game, file));
			}
		}

		return curseFiles;
	}
}
