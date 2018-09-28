package com.therandomlabs.curseapi.cursemeta;

public class AddOnCategorySection implements Cloneable {
	public String ExtraIncludePattern;
	public int GameID;
	public int ID;
	public String InitialInclusionPattern;
	public String Name;
	public PackageType PackageType;
	public String Path;

	@Override
	public AddOnCategorySection clone() {
		try {
			return (AddOnCategorySection) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
