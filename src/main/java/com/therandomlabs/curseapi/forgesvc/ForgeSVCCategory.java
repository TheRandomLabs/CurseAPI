package com.therandomlabs.curseapi.forgesvc;

import java.util.List;
import java.util.Optional;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseGame;
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
	private String slug;
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
	public String slug() {
		if (slug == null) {
			final List<String> pathSegments = url.encodedPathSegments();
			slug = pathSegments.get(pathSegments.size() - 1);
		}

		return slug;
	}

	@Override
	public HttpUrl url() throws CurseException {
		if (url != null) {
			return url;
		}

		final int sectionID = sectionID();

		if (sectionID == 0) {
			final Optional<CurseGame> optionalGame = CurseAPI.game(gameId);

			if (!optionalGame.isPresent()) {
				throw new CurseException("Failed to retrieve URL for category: " + this);
			}

			url = HttpUrl.get(optionalGame.get().url() + "/" + slug);
			return url;
		}

		final Optional<CurseCategory> optionalCategory = CurseAPI.category(sectionID);

		if (!optionalCategory.isPresent()) {
			throw new CurseException("Failed to retrieve URL for category: " + this);
		}

		url = HttpUrl.get(optionalCategory.get().url() + "/" + slug);
		return url;
	}

	@Override
	public HttpUrl avatarURL() {
		return avatarUrl;
	}
}
