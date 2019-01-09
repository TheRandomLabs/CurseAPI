package com.therandomlabs.curseapi.cursemeta;

public final class CMCategorySection implements Cloneable {
	public int Id;
	public int gameId;
	public String name;
	public int packageType;
	public String path;
	public String initialInclusionPattern;
	public String extraIncludePattern;

	@Override
	public CMCategorySection clone() {
		try {
			return (CMCategorySection) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
