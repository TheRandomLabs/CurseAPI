package com.therandomlabs.curseapi.cursemeta;

import java.util.Arrays;

public class CurseMetaStatus implements Cloneable {
	public String status;
	public String message;
	public String[] apis;

	@Override
	public CurseMetaStatus clone() {
		try {
			return (CurseMetaStatus) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[status=" + status + ",message=" + message + ",apis" +
				"=" +
				Arrays.toString(apis) + "]";
	}
}
