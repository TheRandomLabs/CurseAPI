package com.therandomlabs.curseapi.cursemeta;

public class CurseMetaError implements Cloneable {
	public String description;
	public boolean error;
	public int status;

	@Override
	public CurseMetaError clone() {
		try {
			final CurseMetaError cmError = (CurseMetaError) super.clone();

			cmError.description = description;
			cmError.error = error;
			cmError.status = status;

			return cmError;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
