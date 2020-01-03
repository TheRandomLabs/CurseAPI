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

package com.therandomlabs.curseapi.project;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CursePreconditions;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.game.CurseGameVersion;

/**
 * Represents a CurseForge project search query.
 */
public class CurseSearchQuery implements Cloneable {
	private int gameID;
	private int categorySectionID;
	private int categoryID;
	private String gameVersion = "";
	private int pageIndex;
	private int pageSize;
	private String searchFilter = "";
	private CurseSearchSort sortingMethod = CurseSearchSort.FEATURED;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("gameID", gameID).
				add("categorySectionID", categorySectionID).
				add("categoryID", categoryID).
				add("gameVersion", gameVersion).
				add("pageIndex", pageIndex).
				add("pageSize", pageSize).
				add("searchFilter", searchFilter).
				add("sortingMethod", sortingMethod).
				toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseSearchQuery clone() {
		try {
			return (CurseSearchQuery) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns this {@link CurseSearchQuery}'s game ID.
	 *
	 * @return this {@link CurseSearchQuery}'s game ID.
	 */
	public int gameID() {
		return gameID;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s game ID.
	 *
	 * @param id a game ID.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery gameID(int id) {
		CursePreconditions.checkGameID(id, "id");
		gameID = id;
		return this;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s game ID to the ID of the specified {@link CurseGame}.
	 *
	 * @param game a {@link CurseGame}.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery game(CurseGame game) {
		Preconditions.checkNotNull(game, "game should not be null");
		return gameID(game.id());
	}

	/**
	 * Clears this {@link CurseSearchQuery}'s game.
	 *
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery clearGame() {
		gameID = 0;
		return this;
	}

	/**
	 * Returns this {@link CurseSearchQuery}'s category section ID.
	 *
	 * @return this {@link CurseSearchQuery}'s category section ID.
	 */
	public int categorySectionID() {
		return categorySectionID;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s category section ID.
	 *
	 * @param id a category section ID.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery categorySectionID(int id) {
		CursePreconditions.checkCategorySectionID(id, "id");
		categorySectionID = id;
		return this;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s game ID and category section ID to the game ID and
	 * ID of the specified {@link CurseCategorySection}.
	 *
	 * @param section a {@link CurseCategorySection}.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery categorySection(CurseCategorySection section) {
		Preconditions.checkNotNull(section, "section should not be null");
		gameID(section.gameID());
		return categorySectionID(section.id());
	}

	/**
	 * Clears this {@link CurseSearchQuery}'s category section.
	 *
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery clearCategorySection() {
		categorySectionID = 0;
		return this;
	}

	/**
	 * Returns this {@link CurseSearchQuery}'s category ID.
	 *
	 * @return this {@link CurseSearchQuery}'s category ID.
	 */
	public int categoryID() {
		return categoryID;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s category ID.
	 *
	 * @param id a category ID.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery categoryID(int id) {
		CursePreconditions.checkCategoryID(id, "id");
		categoryID = id;
		return this;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s game ID, category section ID and category ID to
	 * the game ID, category section ID and ID of the specified {@link CurseCategory}.
	 *
	 * @param category a {@link CurseCategory}.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery category(CurseCategory category) {
		Preconditions.checkNotNull(category, "category should not be null");
		gameID(category.gameID());
		categorySectionID(category.sectionID());
		return categoryID(category.id());
	}

	/**
	 * Clears this {@link CurseSearchQuery}'s category.
	 *
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery clearCategory() {
		categoryID = 0;
		return this;
	}

	/**
	 * Returns this {@link CurseSearchQuery}'s game version string.
	 *
	 * @return this {@link CurseSearchQuery}'s game version string.
	 */
	public String gameVersionString() {
		return gameVersion;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s game version string.
	 *
	 * @param version a game version string.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery gameVersionString(String version) {
		Preconditions.checkNotNull(version, "version should not be null");
		Preconditions.checkArgument(!version.isEmpty(), "version should not be empty");
		gameVersion = version;
		return this;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s game version.
	 *
	 * @param version a game version.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery gameVersion(CurseGameVersion<?> version) {
		Preconditions.checkNotNull(version, "version should not be null");

		if (gameID != 0) {
			Preconditions.checkArgument(
					version.gameID() == gameID, "Game version should match game ID"
			);
		}

		gameVersion = version.versionString();
		return this;
	}

	/**
	 * Clears this {@link CurseSearchQuery}'s game version string.
	 *
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery clearGameVersionString() {
		gameVersion = "";
		return this;
	}

	/**
	 * Returns this {@link CurseSearchQuery}'s page index.
	 *
	 * @return this {@link CurseSearchQuery}'s page index.
	 */
	public int pageIndex() {
		return pageIndex;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s page index.
	 *
	 * @param index a page index.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery pageIndex(int index) {
		Preconditions.checkArgument(index >= 0, "index should not be smaller than 0");
		pageIndex = index;
		return this;
	}

	/**
	 * Clears this {@link CurseSearchQuery}'s page index.
	 *
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery clearPageIndex() {
		pageIndex = 0;
		return this;
	}

	/**
	 * Returns this {@link CurseSearchQuery}'s page size.
	 *
	 * @return this {@link CurseSearchQuery}'s page size.
	 * @see #pageSize(int)
	 */
	public int pageSize() {
		return pageSize;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s page size.
	 * <p>
	 * This page size seems to serve only as a rough guideline,
	 * and the actual page size often varies.
	 * <p>
	 * If no page size is specified, the default page size seems to be 500 if a search filter
	 * is not specified and 25 if it is.
	 *
	 * @param size a page size.
	 * @return this {@link CurseSearchQuery}.
	 */
	@SuppressWarnings("GrazieInspection")
	public CurseSearchQuery pageSize(int size) {
		Preconditions.checkArgument(size > 0, "size should be positive");
		pageSize = size;
		return this;
	}

	/**
	 * Clears this {@link CurseSearchQuery}'s page size.
	 *
	 * @return this {@link CurseSearchQuery}.
	 * @see #pageSize(int)
	 */
	public CurseSearchQuery clearPageSize() {
		pageSize = 0;
		return this;
	}

	/**
	 * Returns this {@link CurseSearchQuery}'s search filter.
	 *
	 * @return this {@link CurseSearchQuery}'s search filter.
	 */
	public String searchFilter() {
		return searchFilter;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s search filter.
	 *
	 * @param filter a search filter.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery searchFilter(String filter) {
		Preconditions.checkNotNull(filter, "filter should not be null");
		Preconditions.checkArgument(!filter.isEmpty(), "filter should not be empty");
		searchFilter = filter;
		return this;
	}

	/**
	 * Clears this {@link CurseSearchQuery}'s search filter.
	 *
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery clearSearchFilter() {
		searchFilter = "";
		return this;
	}

	/**
	 * Returns this {@link CurseSearchQuery}'s sorting method.
	 * This is {@link CurseSearchSort#FEATURED} by default.
	 *
	 * @return this {@link CurseSearchQuery}'s sorting method.
	 */
	public CurseSearchSort sortingMethod() {
		return sortingMethod;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s sorting method.
	 *
	 * @param method a {@link CurseSearchSort}.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery sortingMethod(CurseSearchSort method) {
		Preconditions.checkNotNull(method, "method should not be null");
		sortingMethod = method;
		return this;
	}

	/**
	 * Clears this {@link CurseSearchQuery}'s sorting method.
	 *
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery clearSortingMethod() {
		sortingMethod = CurseSearchSort.FEATURED;
		return this;
	}
}
