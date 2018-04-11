package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import com.therandomlabs.curseapi.file.FileStatus;
import com.therandomlabs.curseapi.file.ReleaseType;
import com.therandomlabs.curseapi.util.CloneException;

public class AddOnFile implements Cloneable, Serializable {
	private static final long serialVersionUID = -1265896534353648752L;

	public int Id;
	public int AlternateFileId;
	public ArrayList<AddOnFileDependency> Dependencies;
	public URL DownloadURL;
	public String FileDate;
	public String FileName;
	public String FileNameOnDisk;
	public FileStatus FileStatus;
	public String[] GameVersion;
	public boolean IsAlternate;
	public boolean IsAvailable;
	public long PackageFingerprint;
	public ReleaseType ReleaseType;
	public ArrayList<AddOnModule> Modules;

	@SuppressWarnings("unchecked")
	@Override
	public AddOnFile clone() {
		try {
			final AddOnFile file = (AddOnFile) super.clone();

			file.Id = Id;
			file.AlternateFileId = AlternateFileId;
			file.Dependencies = CloneException.tryClone(Dependencies);
			file.DownloadURL = DownloadURL;
			file.FileDate = FileDate;
			file.FileName = FileName;
			file.FileNameOnDisk = FileNameOnDisk;
			file.FileStatus = FileStatus;
			file.GameVersion = GameVersion.clone();
			file.IsAlternate = IsAlternate;
			file.PackageFingerprint = PackageFingerprint;
			file.ReleaseType = ReleaseType;
			file.Modules = CloneException.tryClone(Modules);

			return file;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
