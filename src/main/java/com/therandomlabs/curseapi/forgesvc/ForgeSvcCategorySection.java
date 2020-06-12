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

import java.util.Optional;
import java.util.Set;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
import com.therandomlabs.curseapi.game.CurseGame;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.checkerframework.framework.qual.TypeUseLocation;

//NullAway does not yet support DefaultQualifier, so we have to use SuppressWarning.
@SuppressWarnings("NullAway")
@DefaultQualifier(value = Nullable.class, locations = TypeUseLocation.FIELD)
final class ForgeSvcCategorySection extends CurseCategorySection {
	private int gameId;
	private int gameCategoryId;
	private String name;

	//Cache.
	private transient CurseGame game;
	private transient Set<CurseCategory> categories;
	private transient CurseCategory category;

	@Override
	public int gameID() {
		return gameId;
	}

	@Override
	public CurseGame game() throws CurseException {
		if (game == null) {
			final Optional<CurseGame> optionalGame = CurseAPI.game(gameId);

			if (!optionalGame.isPresent()) {
				throw new CurseException("Could not retrieve game for category section: " + this);
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
	public int id() {
		return gameCategoryId;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Set<CurseCategory> categories() throws CurseException {
		if (categories == null) {
			categories = CurseAPI.categories(gameCategoryId).orElse(null);

			if (categories == null) {
				throw new CurseException(
						"Failed to retrieve categories in category section: " + this
				);
			}
		}

		return categories;
	}

	@Override
	public Set<CurseCategory> refreshCategories() throws CurseException {
		categories = null;
		return categories();
	}

	@Override
	public CurseCategory asCategory() throws CurseException {
		if (category == null) {
			category = CurseAPI.category(gameCategoryId).orElse(null);

			if (category == null) {
				throw new CurseException(
						"Failed to retrieve category section as category: " + this
				);
			}
		}

		return category;
	}

	@Override
	public CurseCategory refreshAsCategory() throws CurseException {
		category = null;
		return asCategory();
	}
}
