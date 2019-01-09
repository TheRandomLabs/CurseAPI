package com.therandomlabs.curseapi.cursemeta;

import java.net.URL;

public final class CMCategory implements Cloneable {
	public int id;
	public String name;
	public URL url;
	public URL avatarUrl;

	@Override
	public CMCategory clone() {
		try {
			return (CMCategory) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
