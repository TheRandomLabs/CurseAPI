package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;

public class AddOnCategorySection implements Cloneable, Serializable {
	private static final long serialVersionUID = 6343148244101167939L;

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
