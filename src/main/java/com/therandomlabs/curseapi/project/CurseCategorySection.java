package com.therandomlabs.curseapi.project;

import java.awt.image.BufferedImage;

import com.google.common.base.MoreObjects;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.OkHttpUtils;
import okhttp3.HttpUrl;

/**
 * Represents a CurseForge category section.
 * <p>
 * Implementations of this interface should be effectively immutable.
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
	 * This method returns true if and only if the other object is also a
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
				add("slug", slug()).
				toString();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * {@link Integer#compare(int, int)} is used on the values returned by
	 * {@link #id()} to determine the value that this method returns.
	 */
	@Override
	public final int compareTo(CurseCategorySection category) {
		return Integer.compare(id(), category.id());
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
	 * Returns this category section's slug.
	 *
	 * @return this category section's slug.
	 */
	public abstract String slug();

	/**
	 * Returns this category section's avatar URL.
	 *
	 * @return this category section's avatar URL.
	 */
	public abstract HttpUrl avatarURL();

	/**
	 * Returns this category section's avatar as a {@link BufferedImage}.
	 *
	 * @return this category section's avatar as a {@link BufferedImage}.
	 * @throws CurseException if an error occurs.
	 */
	public BufferedImage avatar() throws CurseException {
		return OkHttpUtils.readImage(avatarURL());
	}
}
