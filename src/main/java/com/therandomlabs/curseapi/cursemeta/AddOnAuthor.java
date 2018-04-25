package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;
import java.net.URL;

public class AddOnAuthor implements Cloneable, Serializable {
	private static final long serialVersionUID = 2325859921135090509L;

	public String Name;
	public URL Url;

	@Override
	public AddOnAuthor clone() {
		try {
			return (AddOnAuthor) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
