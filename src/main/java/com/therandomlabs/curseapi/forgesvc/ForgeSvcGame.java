package com.therandomlabs.curseapi.forgesvc;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.game.CurseGameVersion;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.checkerframework.framework.qual.TypeUseLocation;

//NullAway does not yet support DefaultQualifier, so we have to use SuppressWarning.
@SuppressWarnings("NullAway")
@DefaultQualifier(value = Nullable.class, locations = TypeUseLocation.FIELD)
final class ForgeSvcGame extends CurseGame {
	private int id;
	private String name;
	private String slug;
	private Set<ForgeSvcCategorySection> categorySections;

	//Cache.
	private transient Set<CurseCategory> categories;
	private transient SortedSet<CurseGameVersion<?>> versions;

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

	@SuppressWarnings("unchecked")
	@Override
	public <V extends CurseGameVersion<?>> SortedSet<V> versions() throws CurseException {
		if (versions == null) {
			versions = CurseAPI.gameVersions(id()).orElseGet(TreeSet::new);
		}

		return (SortedSet<V>) versions;
	}

	@Override
	public void clearVersionsCache() {
		versions = null;
	}
}
