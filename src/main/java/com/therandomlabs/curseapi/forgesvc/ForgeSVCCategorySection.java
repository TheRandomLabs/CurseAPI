package com.therandomlabs.curseapi.forgesvc;

import java.util.HashSet;
import java.util.Set;

import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
import com.therandomlabs.curseapi.util.RetrofitUtils;

final class ForgeSVCCategorySection extends CurseCategorySection {
	private int gameId;
	private int gameCategoryId;
	private String name;

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
		return new HashSet<>(RetrofitUtils.execute(
				ForgeSVCProvider.FORGESVC.getCategories(gameCategoryId)
		));
	}
}
