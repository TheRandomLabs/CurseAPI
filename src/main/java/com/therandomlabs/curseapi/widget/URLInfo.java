package com.therandomlabs.curseapi.widget;

import java.io.Serializable;
import java.net.URL;

public final class URLInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = 8414622257848610580L;

	public URL project;
	public URL curseforge;

	@Override
	public URLInfo clone() {
		final URLInfo info = new URLInfo();

		info.project = project;
		info.curseforge = curseforge;

		return info;
	}
}
