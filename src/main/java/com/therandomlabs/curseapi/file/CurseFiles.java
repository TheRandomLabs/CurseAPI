package com.therandomlabs.curseapi.file;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Predicate;

import com.therandomlabs.curseapi.CursePreconditions;

/**
 * An implementation of {@link TreeSet} with additional utility methods for working with
 * {@link CurseFile}s.
 *
 * @param <F> the type of {@link BasicCurseFile}.
 */
public class CurseFiles<F extends BasicCurseFile> extends TreeSet<F> {
	/**
	 * When used as a {@link Comparator} for a collection of {@link CurseFile}s,
	 * the {@link CurseFile}s are ordered from newest to oldest.
	 *
	 * @see #withComparator(Comparator)
	 */
	public static final Comparator<BasicCurseFile> SORT_BY_NEWEST = BasicCurseFile::compareTo;

	/**
	 * When used as a {@link Comparator} for a collection of {@link CurseFile}s,
	 * the {@link CurseFile}s are ordered from oldest to newest.
	 *
	 * @see #withComparator(Comparator)
	 */
	public static final Comparator<BasicCurseFile> SORT_BY_OLDEST = SORT_BY_NEWEST.reversed();

	private static final long serialVersionUID = -7609834501394579694L;

	/**
	 * Creates an empty {@link CurseFiles} instance ordered from newest to oldest.
	 */
	public CurseFiles() {
		//Default constructor.
	}

	/**
	 * Creates an empty {@link CurseFiles} instance with the specified {@link Comparator}.
	 *
	 * @param comparator a {@link Comparator}.
	 */
	public CurseFiles(Comparator<? super F> comparator) {
		super(comparator);
	}

	/**
	 * Creates a {@link CurseFiles} instance containing all of the {@link CurseFile}s in the
	 * specified collection.
	 *
	 * @param files a collection of {@link CurseFile}s.
	 */
	public CurseFiles(Collection<? extends F> files) {
		super(files);
	}

	/**
	 * Creates a {@link CurseFiles} instance containing all of the {@link CurseFile}s in the
	 * specified collection with the specified {@link Comparator}.
	 *
	 * @param files a collection of {@link CurseFile}s.
	 * @param comparator a {@link Comparator}.
	 */
	public CurseFiles(
			Collection<? extends F> files, Comparator<? super F> comparator
	) {
		super(comparator);
		addAll(files);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CurseFiles<F> clone() {
		return (CurseFiles<F>) super.clone();
	}

	/**
	 * Removes all {@link CurseFile}s in this {@link CurseFiles} that do not match the specified
	 * filter.
	 *
	 * @param filter a {@link Predicate} filter.
	 */
	public void filter(Predicate<? super F> filter) {
		removeIf(filter.negate());
	}

	/**
	 * Returns the {@link CurseFile} instance in this {@link CurseFiles} with the specified ID.
	 *
	 * @param id a file ID.
	 * @return the {@link CurseFile} instance in this {@link CurseFiles} with the specified ID
	 * wrapped in an {@link Optional} if it exists, or otherwise {@link Optional#empty()}.
	 * @see CurseFileFilter
	 */
	public Optional<F> fileWithID(int id) {
		CursePreconditions.checkFileID(id, "id");

		for (F file : this) {
			if (file.id() == file.id()) {
				return Optional.of(file);
			}
		}

		return Optional.empty();
	}

	/**
	 * Returns a copy of this {@link CurseFiles} instance with the specified {@link Comparator}.
	 *
	 * @param comparator a {@link Comparator}.
	 * @return a copy of this {@link CurseFiles} instance with the specified {@link Comparator}.
	 */
	public CurseFiles<F> withComparator(Comparator<? super F> comparator) {
		return new CurseFiles<>(this, comparator);
	}
}
