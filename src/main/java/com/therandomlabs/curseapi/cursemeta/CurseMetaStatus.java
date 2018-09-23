package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;
import java.util.Arrays;

public class CurseMetaStatus implements Cloneable, Serializable {
	private static final long serialVersionUID = -460290357893113377L;

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
