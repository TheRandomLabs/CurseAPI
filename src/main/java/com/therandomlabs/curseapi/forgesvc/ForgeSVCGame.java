package com.therandomlabs.curseapi.forgesvc;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
import com.therandomlabs.curseapi.game.CurseGame;

final class ForgeSVCGame extends CurseGame {
	private int id;
	private String name;
	private String slug;
	private Set<ForgeSVCCategorySection> categorySections;

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
		return ForgeSVCProvider.INSTANCE.categories().stream().
				filter(category -> category.gameID() == id).
				collect(Collectors.toCollection(HashSet::new));
	}
}
