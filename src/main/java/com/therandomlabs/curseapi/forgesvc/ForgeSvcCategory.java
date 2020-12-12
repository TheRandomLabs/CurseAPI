/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2020 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.therandomlabs.curseapi.forgesvc;

import java.util.List;
import java.util.Optional;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseGame;
import okhttp3.HttpUrl;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.checkerframework.framework.qual.TypeUseLocation;

//NullAway does not yet support DefaultQualifier, so we have to use SuppressWarning.
@SuppressWarnings({"ConstantConditions", "NullAway"})
@DefaultQualifier(value = Nullable.class, locations = TypeUseLocation.FIELD)
final class ForgeSvcCategory extends CurseCategory {
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

	//Cache.
	private transient CurseGame game;

	@Override
	public int gameID() {
		return gameId;
	}

	@Override
	public CurseGame game() throws CurseException {
		if (game == null) {
			final Optional<CurseGame> optionalGame = CurseAPI.game(gameId);

			if (!optionalGame.isPresent()) {
				throw new CurseException("Could not retrieve game for category: " + this);
			}

			game = optionalGame.get();
		}

		return game;
	}

	@Override
	public CurseGame refreshGame() throws CurseException {
		game = null;
		return game();
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
	public HttpUrl logoURL() {
		return avatarUrl;
	}
}
