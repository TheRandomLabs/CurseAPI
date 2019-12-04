package com.therandomlabs.curseapi.forgesvc;

import com.therandomlabs.curseapi.game.CurseCategory;
import okhttp3.HttpUrl;

final class ForgeSVCCategory extends CurseCategory {
	private int gameId;

	//This is the usual name.
	private Integer rootGameCategoryId;
	//This is the name in api/v2/addon.
	private int rootId;

	//This is the usual name.
	private int id;
	//This is the name in api/v2/addon.
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
		if (rootId == 0) {
			return rootGameCategoryId == null ? 0 : rootGameCategoryId;
		}

		return rootId;
	}

	@Override
	public int id() {
		return id == 0 ? categoryId : id;
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
