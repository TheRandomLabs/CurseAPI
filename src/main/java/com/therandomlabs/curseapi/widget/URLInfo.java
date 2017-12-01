package com.therandomlabs.curseapi.widget;

import java.net.URL;

public final class URLInfo implements Cloneable {
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
