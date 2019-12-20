package com.therandomlabs.curseapi.forgesvc;

import java.util.Set;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;

final class ForgeSVCCategorySection extends CurseCategorySection {
	private int gameId;
	private int gameCategoryId;
	private String name;

	//Cache.
	private transient Set<CurseCategory> categories;
	private transient CurseCategory category;

	@Override
	public int gameID() {
		return gameId;
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
	public void clearCategoriesCache() {
		categories = null;
	}

	@Override
	public CurseCategory category() throws CurseException {
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
	public void clearCategoryCache() {
		category = null;
	}
}
