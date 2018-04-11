package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;

public class AddOnModule implements Cloneable, Serializable {
	private static final long serialVersionUID = -2811805463510045881L;

	public long Fingerprint;
	public String Foldername;

	@Override
	public AddOnModule clone() {
		try {
			final AddOnModule module = (AddOnModule) super.clone();

			module.Fingerprint = Fingerprint;
			module.Foldername = Foldername;

			return module;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
