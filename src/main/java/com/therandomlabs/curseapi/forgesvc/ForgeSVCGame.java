package com.therandomlabs.curseapi.forgesvc;

import java.util.HashSet;
import java.util.Set;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
import com.therandomlabs.curseapi.game.CurseGame;

final class ForgeSVCGame extends CurseGame {
	private int id;
	private String name;
	private String slug;
	private Set<ForgeSVCCategorySection> categorySections;

	//Cache.
	private transient Set<CurseCategory> categories;

	@Override
	public int id() {
		return id;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String slug() {
		return slug;
	}

	@Override
	public Set<CurseCategorySection> categorySections() {
		return new HashSet<>(categorySections);
	}

	@Override
	public Set<CurseCategory> categories() throws CurseException {
		if (categories == null) {
			categories = CurseAPI.categories().orElse(null);

			if (categories == null) {
				throw new CurseException("Failed to retrieve categories in game: " + this);
			}

			categories.removeIf(category -> category.gameID() != id);
		}

		return categories;
	}

	@Override
	public void clearCategoriesCache() {
		categories = null;
	}
}
