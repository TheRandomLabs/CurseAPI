package com.therandomlabs.curseapi.cursemeta;

public class CurseMetaError implements Cloneable {
	public String description;
	public boolean error;
	public int status;

	@Override
	public CurseMetaError clone() {
		try {
			return (CurseMetaError) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
