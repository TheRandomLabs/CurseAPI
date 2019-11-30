package com.therandomlabs.curseapi.forgesvc;

import okhttp3.HttpUrl;

public final class ForgeSVCAttachment {
	private int id;
	private String title;
	private String description;
	private HttpUrl url;
	private HttpUrl thumbnailUrl;
	private boolean isDefault;

	public int id() {
		return id;
	}

	public String title() {
		return title;
	}

	public String description() {
		return description;
	}

	public HttpUrl url() {
		return url;
	}

	public HttpUrl thumbnailURL() {
		return thumbnailUrl;
	}

	public boolean isDefault() {
		return isDefault;
	}
}
