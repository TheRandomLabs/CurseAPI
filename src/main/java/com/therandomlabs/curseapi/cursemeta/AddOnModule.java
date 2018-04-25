package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;

public class AddOnModule implements Cloneable, Serializable {
	private static final long serialVersionUID = -2811805463510045881L;

	public long Fingerprint;
	public String Foldername;

	@Override
	public AddOnModule clone() {
		try {
			return (AddOnModule) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
