package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.therandomlabs.curseapi.file.FileStatus;
import com.therandomlabs.curseapi.file.ReleaseType;
import com.therandomlabs.curseapi.util.CloneException;

public class AddOnFile implements Cloneable, Serializable {
	private static final long serialVersionUID = -1265896534353648752L;

	public int id;
	public int alternateFileId;
	public List<AddOnFileDependency> dependencies;
	public URL downloadURL;
	public String fileDate;
	public String fileName;
	public String fileNameOnDisk;
	public FileStatus fileStatus;
	public ArrayList<String> gameVersion;
	public boolean isAlternate;
	public boolean isAvailable;
	public long packageFingerprint;
	public ReleaseType releaseType;
	public List<AddOnModule> modules;

	@SuppressWarnings("unchecked")
	@Override
	public AddOnFile clone() {
		try {
			final AddOnFile file = (AddOnFile) super.clone();

			file.id = id;
			file.alternateFileId = alternateFileId;
			file.dependencies = CloneException.tryClone(dependencies);
			file.downloadURL = downloadURL;
			file.fileDate = fileDate;
			file.fileName = fileName;
			file.fileNameOnDisk = fileNameOnDisk;
			file.fileStatus = fileStatus;
			file.gameVersion = (ArrayList<String>) gameVersion.clone();
			file.isAlternate = isAlternate;
			file.packageFingerprint = packageFingerprint;
			file.releaseType = releaseType;
			file.modules = CloneException.tryClone(modules);

			return file;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
