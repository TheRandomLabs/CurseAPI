package com.therandomlabs.curseapi.cursemeta;

import com.therandomlabs.curseapi.file.ReleaseType;

public class AddOnLatestFiles {
	public String FileType;
	public String GameVesion; //Spelt wrong in CurseMeta
	public int ProjectFileID;
	public String ProjectFileName;

	@Override
	public AddOnLatestFiles clone() {
		try {
			return (AddOnLatestFiles) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	public ReleaseType releaseType() {
		return ReleaseType.fromName(FileType);
	}
}
