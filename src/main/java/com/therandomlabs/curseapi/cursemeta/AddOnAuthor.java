package com.therandomlabs.curseapi.cursemeta;

import java.net.URL;

public class AddOnAuthor implements Cloneable {
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
