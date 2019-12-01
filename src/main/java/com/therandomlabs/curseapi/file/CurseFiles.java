package com.therandomlabs.curseapi.file;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseAPI;

/**
 * An implementation of {@link TreeSet} with additional utility methods for working with
 * {@link CurseFile}s.
 */
public class CurseFiles extends TreeSet<CurseFile> {
	/**
	 * When used as a {@link Comparator} for a collection of {@link CurseFile}s,
	 * the {@link CurseFile}s are ordered from newest to oldest.
	 *
	 * @see #withComparator(Comparator)
	 */
	public static final Comparator<CurseFile> SORT_BY_NEWEST = CurseFile::compareTo;

	/**
	 * When used as a {@link Comparator} for a collection of {@link CurseFile}s,
	 * the {@link CurseFile}s are ordered from oldest to newest.
	 *
	 * @see #withComparator(Comparator)
	 */
	public static final Comparator<CurseFile> SORT_BY_OLDEST = SORT_BY_NEWEST.reversed();

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
	public CurseFiles(Comparator<? super CurseFile> comparator) {
		super(comparator);
	}

	/**
	 * Creates a {@link CurseFiles} instance containing all of the {@link CurseFile}s in the
	 * specified collection.
	 *
	 * @param files a collection of {@link CurseFile}s.
	 */
	public CurseFiles(Collection<? extends CurseFile> files) {
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
			Collection<? extends CurseFile> files, Comparator<? super CurseFile> comparator
	) {
		super(comparator);
		addAll(files);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseFiles clone() {
		return (CurseFiles) super.clone();
	}

	/**
	 * Removes all {@link CurseFile}s in this {@link CurseFiles} that do not match the specified
	 * filter.
	 *
	 * @param filter a {@link Predicate} filter.
	 */
	public void filter(Predicate<? super CurseFile> filter) {
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
	public Optional<CurseFile> fileWithID(int id) {
		Preconditions.checkArgument(
				id >= CurseAPI.MIN_FILE_ID, "id should not be smaller than %s",
				CurseAPI.MIN_FILE_ID
		);

		for (CurseFile file : this) {
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
	public CurseFiles withComparator(Comparator<? super CurseFile> comparator) {
		return new CurseFiles(this, comparator);
	}
}
