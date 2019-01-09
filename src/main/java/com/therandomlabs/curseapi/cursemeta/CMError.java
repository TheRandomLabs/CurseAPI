package com.therandomlabs.curseapi.cursemeta;

public final class CMError implements Cloneable {
	public String description;
	public boolean error;
	public int status;

	@Override
	public CMError clone() {
		try {
			return (CMError) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
