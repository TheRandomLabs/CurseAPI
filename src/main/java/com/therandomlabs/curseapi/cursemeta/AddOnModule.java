package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;

public class AddOnModule implements Cloneable, Serializable {
	private static final long serialVersionUID = -2811805463510045881L;

	public long fingerprint;
	public String foldername;

	@Override
	public AddOnModule clone() {
		try {
			final AddOnModule module = (AddOnModule) super.clone();

			module.fingerprint = fingerprint;
			module.foldername = foldername;

			return module;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
