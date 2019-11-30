package com.therandomlabs.curseapi;

import java.awt.image.BufferedImage;

import com.google.common.base.MoreObjects;
import com.therandomlabs.curseapi.util.OkHttpUtils;
import okhttp3.HttpUrl;

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
	 * This method returns true if and only if the other object is also a {@link CurseCategory} and
	 * the value returned by {@link #id()} is the same for both {@link CurseCategory}s.
	 */
	@Override
	public final boolean equals(Object object) {
		return this == object ||
				(object instanceof CurseCategory && id() == ((CurseCategory) object).id());
	}

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
	 * {@link Integer#compare(int, int)} is used on the values returned by
	 * {@link #id()} to determine the value that this method returns.
	 */
	@Override
	public final int compareTo(CurseCategory category) {
		return Integer.compare(id(), category.id());
	}

	public abstract int gameID();

	public abstract int id();

	public abstract String name();

	public abstract HttpUrl url();

	public abstract HttpUrl avatarURL();

	public BufferedImage avatar() throws CurseException {
		return OkHttpUtils.readImage(avatarURL());
	}
}
