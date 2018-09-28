package com.therandomlabs.curseapi.cursemeta;

import java.net.URL;

public class AddOnAttachment implements Cloneable {
	public String Description;
	public boolean IsDefault;
	public URL ThumbnailUrl;
	public String Title;
	public URL Url;

	@Override
	public AddOnAttachment clone() {
		try {
			return (AddOnAttachment) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
