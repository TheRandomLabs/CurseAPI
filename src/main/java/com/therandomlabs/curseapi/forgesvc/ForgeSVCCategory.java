package com.therandomlabs.curseapi.forgesvc;

import com.therandomlabs.curseapi.game.CurseCategory;
import okhttp3.HttpUrl;

final class ForgeSVCCategory extends CurseCategory {
	private int gameId;
	private int rootGameCategoryId;
	private int categoryId;
	private String name;
	private HttpUrl url;
	private HttpUrl avatarUrl;

	@Override
	public int gameID() {
		return gameId;
	}

	@Override
	public int sectionID() {
		return rootGameCategoryId;
	}

	@Override
	public int id() {
		return categoryId;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public HttpUrl url() {
		return url;
	}

	@Override
	public HttpUrl avatarURL() {
		return avatarUrl;
	}
}
