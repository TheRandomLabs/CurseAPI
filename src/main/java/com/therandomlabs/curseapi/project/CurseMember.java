package com.therandomlabs.curseapi.project;

import com.google.common.base.MoreObjects;
import okhttp3.HttpUrl;

/**
 * Represents a CurseForge project member.
 * <p>
 * Implementations of this class should be effectively immutable.
 */
public abstract class CurseMember implements Comparable<CurseMember> {
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
	 * {@link CurseMember} and the value returned by {@link #id()} is the same for both
	 * {@link CurseMember}s.
	 */
	@Override
	public final boolean equals(Object object) {
		return this == object ||
				(object instanceof CurseMember && id() == ((CurseMember) object).id());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
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
	public final int compareTo(CurseMember member) {
		return name().compareTo(member.name());
	}

	/**
	 * Returns this member's ID.
	 *
	 * @return this member's ID.
	 */
	public abstract int id();

	/**
	 * Returns this member's name.
	 *
	 * @return this member's name.
	 */
	public abstract String name();

	/**
	 * Returns this member's URL.
	 *
	 * @return this member's URL.
	 */
	public abstract HttpUrl url();
}
