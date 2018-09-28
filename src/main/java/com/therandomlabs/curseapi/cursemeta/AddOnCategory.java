package com.therandomlabs.curseapi.cursemeta;

import java.net.URL;

public class AddOnCategory implements Cloneable {
	public int Id;
	public String Name;
	public URL URL;

	@Override
	public AddOnCategory clone() {
		try {
			return (AddOnCategory) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
