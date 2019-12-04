package com.therandomlabs.curseapi.game;

import java.awt.image.BufferedImage;

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
				add("url", url()).
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
	 * Returns the ID of the game which this category belongs in.
	 *
	 * @return the ID of the game which this category belongs in.
	 */
	public abstract int gameID();

	/**
	 * Returns the ID of the section which this category belongs in.
	 *
	 * @return the ID of the section which tihs category belongs in,
	 * or {@code 0} if it does not belong in any section.
	 */
	public abstract int sectionID();

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
	 * Returns this category's URL.
	 *
	 * @return this category's URL.
	 */
	public abstract HttpUrl url();

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
