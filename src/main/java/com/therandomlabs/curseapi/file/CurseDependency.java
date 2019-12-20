package com.therandomlabs.curseapi.file;

import com.google.common.base.MoreObjects;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.project.CurseProject;

/**
 * Represents a CurseForge file dependency.
 * <p>
 * Implementations of this class should be effectively immutable.
 */
public abstract class CurseDependency implements Comparable<CurseDependency> {
	/**
	 * {@inheritDoc}
	 * <p>
	 * Calling this method is equivalent to calling {@link #projectID()}.
	 */
	@Override
	public final int hashCode() {
		return projectID();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method returns {@code true} if and only if the other object is also a
	 * {@link CurseDependency} and the value returned by {@link #projectID()} is the same for both
	 * {@link CurseDependency}s.
	 */
	@Override
	public final boolean equals(Object object) {
		return this == object || (object instanceof CurseDependency &&
				projectID() == ((CurseDependency) object).projectID());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("projectID", projectID()).
				add("type", type()).
				toString();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * {@link Integer#compare(int, int)} is used on the values returned by
	 * {@link #projectID()} to determine the value that this method returns.
	 */
	@Override
	public final int compareTo(CurseDependency game) {
		return Integer.compare(projectID(), game.projectID());
	}

	/**
	 * Returns the project ID of this {@link CurseDependency}.
	 *
	 * @return the project ID of this {@link CurseDependency}.
	 */
	public abstract int projectID();

	/**
	 * Returns this {@link CurseDependency} as a {@link CurseProject}. This value may be cached.
	 *
	 * @return this {@link CurseDependency} as a {@link CurseProject}.
	 * @throws CurseException if an error occurs.
	 * @see #clearProjectCache()
	 */
	public abstract CurseProject project() throws CurseException;

	/**
	 * If this {@link CurseDependency} implementation caches the value returned by
	 * {@link #project()}, this method clears this cached value.
	 */
	public abstract void clearProjectCache();

	/**
	 * Returns the {@link CurseFile} from which this {@link CurseDependency} has been retrieved.
	 * @return the {@link CurseFile} from which this {@link CurseDependency} has been retrieved.
	 */
	public abstract CurseFile dependent();

	/**
	 * Returns the type of this {@link CurseDependency}.
	 *
	 * @return the type of this {@link CurseDependency}.
	 */
	public abstract CurseDependencyType type();
}
