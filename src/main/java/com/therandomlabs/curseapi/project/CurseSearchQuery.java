package com.therandomlabs.curseapi.project;

import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CursePreconditions;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
import com.therandomlabs.curseapi.game.CurseGame;

/**
 * Represents a CurseForge project search query.
 */
public class CurseSearchQuery implements Cloneable {
	private int gameID;
	private int categorySectionID;
	private int categoryID;
	private String gameVersion = "";
	private int pageIndex;
	//500 is the default page size if none is specified.
	private int pageSize = 500;
	private String searchFilter = "";
	private CurseSearchSort sortingMethod = CurseSearchSort.FEATURED;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseSearchQuery clone() {
		try {
			return (CurseSearchQuery) super.clone();
		} catch (CloneNotSupportedException ignored) {}

		return null;
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
	 * Returns this {@link CurseSearchQuery}'s game version.
	 *
	 * @return this {@link CurseSearchQuery}'s game version.
	 */
	public String gameVersion() {
		return gameVersion;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s game version.
	 *
	 * @param version a game version.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery gameVersion(String version) {
		Preconditions.checkNotNull(version, "version should not be null");
		Preconditions.checkArgument(!version.isEmpty(), "version should not be empty");
		gameVersion = version;
		return this;
	}

	/**
	 * Clears this {@link CurseSearchQuery}'s game version.
	 *
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery clearGameVersion() {
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
	 */
	public int pageSize() {
		return pageSize;
	}

	/**
	 * Sets this {@link CurseSearchQuery}'s page size.
	 *
	 * @param size a page size.
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery pageSize(int size) {
		Preconditions.checkArgument(size > 0, "size should be positive");
		pageSize = size;
		return this;
	}

	/**
	 * Clears this {@link CurseSearchQuery}'s page size.
	 *
	 * @return this {@link CurseSearchQuery}.
	 */
	public CurseSearchQuery clearPageSize() {
		pageSize = 500;
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
