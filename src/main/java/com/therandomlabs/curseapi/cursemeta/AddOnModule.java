package com.therandomlabs.curseapi.cursemeta;

public class AddOnModule implements Cloneable {
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
