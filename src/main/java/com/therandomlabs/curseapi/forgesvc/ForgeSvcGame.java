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
