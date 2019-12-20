package com.therandomlabs.curseapi.game;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.google.common.base.MoreObjects;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.OkHttpUtils;
import okhttp3.HttpUrl;

/**
 * Represents a CurseForge category.
 * <p>
 * Implementations of this class should be effectively immutable.
 */
public abstract class CurseCategory implements Comparable<CurseCategory> {
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
	 * {@link CurseCategory} and the value returned by {@link #id()} is the same for both
	 * {@link CurseCategory}s.
	 */
	@Override
	public final boolean equals(Object object) {
		return this == object ||
				(object instanceof CurseCategory && id() == ((CurseCategory) object).id());
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
				add("slug", slug()).
				toString();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * {@link String#compareTo(String)} is used on the values returned by
	 * {@link #name()} to determine the value that this method returns.
	 */
	@Override
	public final int compareTo(CurseCategory category) {
		return name().compareTo(category.name());
	}

	/**
	 * Returns the ID of this category's game.
	 *
	 * @return the ID of this category's game.
	 */
	public abstract int gameID();

	/**
	 * Returns this category's game. This value may be cached.
	 *
	 * @return a {@link CurseGame} instance that represents this category's game.
	 * @throws CurseException if an error occurs.
	 */
	public abstract CurseGame game() throws CurseException;

	/**
	 * If this {@link CurseCategory} implementation caches the value returned by
	 * {@link #game()}, this method clears this cached value.
	 */
	public abstract void clearGameCache();

	/**
	 * Returns the ID of this category's section.
	 *
	 * @return the ID of this category's section, or {@code 0} if it does not belong in any section.
	 */
	public abstract int sectionID();

	/**
	 * Returns this category's section. This method uses the value returned by {@link #game()}
	 * to retrieve the category section, so this value may be cached.
	 *
	 * @return a {@link CurseCategorySection} instance that represents this category's section.
	 * @throws CurseException if an error occurs.
	 */
	public CurseCategorySection section() throws CurseException {
		final Optional<CurseCategorySection> optionalSection = game().categorySection(sectionID());

		if (optionalSection.isPresent()) {
			return optionalSection.get();
		}

		throw new CurseException("Could not retrieve section for category: " + this);
	}

	/**
	 * Returns this category's ID.
	 *
	 * @return this category's ID.
	 */
	public abstract int id();

	/**
	 * Returns this category's name.
	 *
	 * @return this category's name.
	 */
	public abstract String name();

	/**
	 * Returns this category's slug.
	 *
	 * @return this category's slug.
	 */
	public abstract String slug();

	/**
	 * Returns this category's URL.
	 *
	 * @return this category's URL.
	 * @throws CurseException if an error occurs.
	 */
	public abstract HttpUrl url() throws CurseException;

	/**
	 * Returns this category's avatar URL.
	 *
	 * @return this category's avatar URL.
	 */
	public abstract HttpUrl avatarURL();

	/**
	 * Returns this category's avatar as a {@link BufferedImage}.
	 *
	 * @return this category's avatar as a {@link BufferedImage}.
	 * @throws CurseException if an error occurs.
	 */
	public BufferedImage avatar() throws CurseException {
		return OkHttpUtils.readImage(avatarURL());
	}
}
