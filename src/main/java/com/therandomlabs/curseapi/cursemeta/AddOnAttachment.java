package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;
import java.net.URL;

public class AddOnAttachment implements Cloneable, Serializable {
	private static final long serialVersionUID = -621722407221049835L;

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
