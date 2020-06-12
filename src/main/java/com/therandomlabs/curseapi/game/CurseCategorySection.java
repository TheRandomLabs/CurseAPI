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

package com.therandomlabs.curseapi.game;

import java.util.Set;

import com.google.common.base.MoreObjects;
import com.therandomlabs.curseapi.CurseException;

/**
 * Represents a CurseForge category section.
 * <p>
 * Implementations of this class should be effectively immutable.
 */
public abstract class CurseCategorySection implements Comparable<CurseCategorySection> {
	/**
	 * {@inheritDoc}
	 * <p>
	 * Calling this method is equivalent to calling {@link #id()}.
	 */
	@Override
	public final int hashCode() {
		return id();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method returns {@code true} if and only if the other object is also a
	 * {@link CurseCategorySection} and the value returned by {@link #id()} is the same for both
	 * {@link CurseCategorySection}s.
	 */
	@Override
	public final boolean equals(Object object) {
		return this == object || (object instanceof CurseCategorySection &&
				id() == ((CurseCategorySection) object).id());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("gameID", gameID()).
				add("id", id()).
				add("name", name()).
				toString();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * {@link String#compareTo(String)} is used on the values returned by
	 * {@link #name()} to determine the value that this method returns.
	 */
	@Override
	public final int compareTo(CurseCategorySection section) {
		return name().compareTo(section.name());
	}

	/**
	 * Returns the ID of this category section's game.
	 *
	 * @return the ID of this category section's game.
	 */
	public abstract int gameID();

	/**
	 * Returns this category section's game.
	 * If this implementation of {@link CurseCategorySection} caches this value,
	 * it may be refreshed by calling {@link #refreshGame()}.
	 *
	 * @return a {@link CurseGame} instance that represents this category section's game.
	 * @throws CurseException if an error occurs.
	 */
	public abstract CurseGame game() throws CurseException;

	/**
	 * If this {@link CurseCategorySection} implementation caches the value returned by
	 * {@link #game()}, this method refreshes this value and returns it.
	 *
	 * @return the refreshed value returned by {@link #game()}.
	 * @throws CurseException if an error occurs.
	 */
	public abstract CurseGame refreshGame() throws CurseException;

	/**
	 * Returns this category section's ID.
	 *
	 * @return this category section's ID.
	 */
	public abstract int id();

	/**
	 * Returns this category section's name.
	 *
	 * @return this category section's name.
	 */
	public abstract String name();

	/**
	 * Returns this category section's categories.
	 * If this {@link CurseCategorySection} implementation caches this value,
	 * it may be refreshed by calling {@link #refreshCategories()}.
	 *
	 * @return this category section's categories.
	 * @throws CurseException if an error occurs.
	 */
	public abstract Set<CurseCategory> categories() throws CurseException;

	/**
	 * If this {@link CurseCategorySection} implementation caches the value returned by
	 * {@link #categories()}, this method refreshes this value and returns it.
	 *
	 * @return the refreshed value returned by {@link #categories()}.
	 * @throws CurseException if an error occurs.
	 */
	public abstract Set<CurseCategory> refreshCategories() throws CurseException;

	/**
	 * Returns this {@link CurseCategorySection} as a {@link CurseCategory}.
	 * If this {@link CurseCategorySection} implementation caches this value,
	 * it may be refreshed by calling {@link #refreshAsCategory()}.
	 *
	 * @return this {@link CurseCategorySection} as a {@link CurseCategory}.
	 * @throws CurseException if an error occurs.
	 */
	public abstract CurseCategory asCategory() throws CurseException;

	/**
	 * If this {@link CurseCategorySection} implementation caches the value returned by
	 * {@link #asCategory()}, this method refreshes this value and returns it.
	 *
	 * @return the refreshed value returned by {@link #asCategory()}.
	 * @throws CurseException if an error occurs.
	 */
	public abstract CurseCategory refreshAsCategory() throws CurseException;
}
