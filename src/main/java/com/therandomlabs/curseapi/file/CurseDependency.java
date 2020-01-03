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

package com.therandomlabs.curseapi.file;

import com.google.common.base.MoreObjects;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.project.CurseProject;

/**
 * Represents a CurseForge file dependency.
 * <p>
 * Implementations of this class should be effectively immutable.
 */
public abstract class CurseDependency {
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
