package com.therandomlabs.curseapi.cursemeta;

public final class CMLatestFile implements Cloneable {
	public String gameVersion;
	public int projectFileId;
	public String projectFileName;
	public int fileType;

	@Override
	public CMLatestFile clone() {
		try {
			return (CMLatestFile) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
