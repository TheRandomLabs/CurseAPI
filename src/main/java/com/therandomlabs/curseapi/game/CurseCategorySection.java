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
	public final int compareTo(CurseCategorySection categorySection) {
		return name().compareTo(categorySection.name());
	}

	/**
	 * Returns the ID of the game which this category section belongs in.
	 *
	 * @return the ID of the game which this category section belongs in.
	 */
	public abstract int gameID();

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
	 *
	 * @return this category section's categories.
	 * @throws CurseException if an error occurs.
	 */
	public abstract Set<CurseCategory> categories() throws CurseException;

	/**
	 * Returns this {@link CurseCategorySection} as a {@link CurseCategory}.
	 *
	 * @return this {@link CurseCategorySection} as a {@link CurseCategory}.
	 * @throws CurseException if an error occurs.
	 */
	public abstract CurseCategory asCategory() throws CurseException;
}
