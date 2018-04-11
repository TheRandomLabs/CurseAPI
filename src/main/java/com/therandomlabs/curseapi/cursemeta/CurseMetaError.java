package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;

public class CurseMetaError implements Cloneable, Serializable {
	private static final long serialVersionUID = -4293007719469923336L;

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
