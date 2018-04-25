package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;
import java.net.URL;

public class AddOnCategory implements Cloneable, Serializable {
	private static final long serialVersionUID = 4166502784471222945L;

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
