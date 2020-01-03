package com.therandomlabs.curseapi.forgesvc;

import okhttp3.HttpUrl;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.checkerframework.framework.qual.TypeUseLocation;

//NullAway does not yet support DefaultQualifier, so we have to use SuppressWarning.
@SuppressWarnings("NullAway")
@DefaultQualifier(value = Nullable.class, locations = TypeUseLocation.FIELD)
final class ForgeSvcAttachment {
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
