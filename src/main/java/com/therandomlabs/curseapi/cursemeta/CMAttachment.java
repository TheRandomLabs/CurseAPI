package com.therandomlabs.curseapi.cursemeta;

import java.net.URL;

public final class CMAttachment implements Cloneable {
	public int id;
	public int projectID;
	public String description;
	public boolean isDefault;
	public URL thumbnailUrl;
	public String title;
	public URL url;

	@Override
	public CMAttachment clone() {
		try {
			return (CMAttachment) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
