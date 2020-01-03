package com.therandomlabs.curseapi.game;

import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.base.MoreObjects;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CursePreconditions;
import okhttp3.HttpUrl;

/**
 * Represents a game supported by CurseForge.
 * <p>
 * Implementations of this class should be effectively immutable.
 */
public abstract class CurseGame implements Comparable<CurseGame> {
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
	 * {@link CurseGame} and the value returned by {@link #id()} is the same for both
	 * {@link CurseGame}s.
	 */
	@Override
	public final boolean equals(Object object) {
		return this == object || (object instanceof CurseGame && id() == ((CurseGame) object).id());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
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
	public final int compareTo(CurseGame game) {
		return name().compareTo(game.name());
	}

	/**
	 * Returns the ID of this {@link CurseGame}.
	 *
	 * @return the ID of this {@link CurseGame}.
	 */
	public abstract int id();

	/**
	 * Returns the name of this {@link CurseGame}.
	 *
	 * @return the name of this {@link CurseGame}.
	 */
	public abstract String name();

	/**
	 * Returns the slug of this {@link CurseGame}.
	 *
	 * @return the slug of this {@link CurseGame}.
	 */
	public abstract String slug();

	/**
	 * Returns the URL of this {@link CurseGame}.
	 *
	 * @return the URL of this {@link CurseGame}.
	 */
	public HttpUrl url() {
		return HttpUrl.get("https://www.curseforge.com/" + slug());
	}

	/**
	 * Returns this {@link CurseGame}'s category sections.
	 *
	 * @return a mutable {@link Set} that contains this {@link CurseGame}'s category sections
	 * as {@link CurseCategorySection}s.
	 */
	public abstract Set<CurseCategorySection> categorySections();

	/**
	 * Returns this {@link CurseGame}'s category section with the specified ID.
	 *
	 * @param id a category section ID.
	 * @return this {@link CurseGame}'s category section with the specified ID wrapped in an
	 * {@link Optional} if it exists, or otherwise an empty {@link Optional}}.
	 */
	public Optional<CurseCategorySection> categorySection(int id) {
		CursePreconditions.checkCategorySectionID(id, "id");
		return categorySections().stream().filter(section -> section.id() == id).findAny();
	}

	/**
	 * Returns this {@link CurseGame}'s categories. This value may be cached.
	 *
	 * @return a mutable {@link Set} that contains this {@link CurseGame}'s categories
	 * as {@link CurseCategory}s.
	 * @throws CurseException if an error occurs.
	 */
	public abstract Set<CurseCategory> categories() throws CurseException;

	/**
	 * If this {@link CurseGame} implementation caches the value returned by {@link #categories()},
	 * this method clears this cached value.
	 */
	public abstract void clearCategoriesCache();

	/**
	 * Returns all known versions of this {@link CurseGame}. This value may be cached.
	 *
	 * @param <V> the implementation of {@link CurseGameVersion}.
	 * @return a mutable {@link SortedSet} of {@link CurseGameVersion} instances equivalent to
	 * the result retrieved by calling {@link CurseAPI#gameVersions(int)}.
	 * If there is no registered {@link com.therandomlabs.curseapi.CurseAPIProvider} implementation
	 * that provides {@link CurseGameVersion}s for this game, an empty {@link SortedSet} is
	 * returned.
	 * @throws CurseException if an error occurs.
	 */
	public abstract <V extends CurseGameVersion<?>> SortedSet<V> versions() throws CurseException;

	/**
	 * If this {@link CurseGame} implementation caches the value returned by
	 * {@link #versions()}, this method clears this cached value.
	 */
	public abstract void clearVersionsCache();
}
