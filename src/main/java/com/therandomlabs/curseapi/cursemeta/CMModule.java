package com.therandomlabs.curseapi.cursemeta;

public final class CMModule implements Cloneable {
	public String moduleName;
	public long fimgerprint; //Misspelt in CurseMeta

	@Override
	public CMModule clone() {
		try {
			return (CMModule) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
