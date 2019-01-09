package com.therandomlabs.curseapi.cursemeta;

import java.net.URL;

public final class CMAuthor implements Cloneable {
	public String name;
	public URL url;

	@Override
	public CMAuthor clone() {
		try {
			return (CMAuthor) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
