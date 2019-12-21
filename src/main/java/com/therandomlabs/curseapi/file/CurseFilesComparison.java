package com.therandomlabs.curseapi.file;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Preconditions;

/**
 * Represents a comparison between two sets of {@link BasicCurseFile}s.
 *
 * @param <F> the type of {@link BasicCurseFile}.
 */
@SuppressWarnings("PMD.LooseCoupling")
public class CurseFilesComparison<F extends BasicCurseFile> {
	private final CurseFiles<F> unchanged;
	private final HashSet<CurseFileChange<F>> updated;
	private final HashSet<CurseFileChange<F>> downgraded;
	private final CurseFiles<F> removed;
	private final CurseFiles<F> added;

	/**
	 * Constructs a {@link CurseFilesComparison}.
	 *
	 * @param unchanged the unchanged files.
	 * @param updated the updated files.
	 * @param downgraded the downgraded files.
	 * @param removed the removed files.
	 * @param added the added files.
	 */
	protected CurseFilesComparison(
			Collection<F> unchanged, Collection<CurseFileChange<F>> updated,
			Collection<CurseFileChange<F>> downgraded, Collection<F> removed, Collection<F> added
	) {
		Preconditions.checkNotNull(unchanged, "unchanged should not be null");
		Preconditions.checkNotNull(updated, "updated should not be null");
		Preconditions.checkNotNull(downgraded, "downgraded should not be null");
		Preconditions.checkNotNull(removed, "removed should not be null");
		Preconditions.checkNotNull(added, "added should not be null");
		this.unchanged = new CurseFiles<>(unchanged);
		this.updated = new HashSet<>(updated);
		this.downgraded = new HashSet<>(downgraded);
		this.removed = new CurseFiles<>(removed);
		this.added = new CurseFiles<>(added);
	}

	/**
	 * Returns a {@link CurseFiles} containing all unchanged files.
	 *
	 * @return a {@link CurseFiles} containing all unchanged files.
	 */
	public CurseFiles<F> unchanged() {
		return unchanged.clone();
	}

	/**
	 * Returns a mutable {@link Set} containing all updated files.
	 *
	 * @return a mutable {@link Set} containing all updated files.
	 */
	@SuppressWarnings("unchecked")
	public Set<CurseFileChange<F>> updated() {
		return (Set<CurseFileChange<F>>) updated.clone();
	}

	/**
	 * Returns a mutable {@link Set} containing all downgraded files.
	 *
	 * @return a mutable {@link Set} containing all downgraded files.
	 */
	@SuppressWarnings("unchecked")
	public Set<CurseFileChange<F>> downgraded() {
		return (Set<CurseFileChange<F>>) downgraded.clone();
	}

	/**
	 * Returns a {@link CurseFiles} containing all removed files.
	 *
	 * @return a {@link CurseFiles} containing all removed files.
	 */
	public CurseFiles<F> removed() {
		return removed.clone();
	}

	/**
	 * Returns a {@link CurseFiles} containing all added files.
	 *
	 * @return a {@link CurseFiles} containing all added files.
	 */
	public CurseFiles<F> added() {
		return added.clone();
	}

	/**
	 * Returns a {@link CurseFilesComparison} instance that represents a comparison between
	 * an old collection of files and a new collection of files.
	 * Files in the old collection may be newer than files of the same project in the new
	 * collection.
	 *
	 * @param oldFiles an old collection of files.
	 * @param newFiles a new collection of files.
	 * @param <F> the type of {@link BasicCurseFile}.
	 * @return a {@link CurseFilesComparison} instance that represents a comparison between
	 * the specified collections of files.
	 */
	public static <F extends BasicCurseFile> CurseFilesComparison<F> of(
			Collection<? extends F> oldFiles, Collection<? extends F> newFiles
	) {
		Preconditions.checkNotNull(oldFiles, "oldFiles should not be null");
		Preconditions.checkNotNull(newFiles, "newFiles should not be null");

		final Set<F> unchanged = new HashSet<>();
		final Set<CurseFileChange<F>> updated = new HashSet<>();
		final Set<CurseFileChange<F>> downgraded = new HashSet<>();
		final Set<F> removed = new HashSet<>();
		final Set<F> added = new HashSet<>();

		for (F oldFile : oldFiles) {
			final Optional<? extends F> optionalNewFile =
					newFiles.stream().filter(oldFile::sameProject).findAny();

			if (!optionalNewFile.isPresent()) {
				removed.add(oldFile);
				continue;
			}

			final F newFile = optionalNewFile.get();

			if (oldFile.equals(newFile)) {
				unchanged.add(newFile);
				break;
			}

			final CurseFileChange<F> fileChange = new CurseFileChange<>(oldFile, newFile);

			if (fileChange.isDowngrade()) {
				downgraded.add(fileChange);
			} else {
				updated.add(fileChange);
			}
		}

		for (F newFile : newFiles) {
			if (oldFiles.stream().noneMatch(newFile::sameProject)) {
				added.add(newFile);
			}
		}

		return new CurseFilesComparison<>(unchanged, updated, downgraded, removed, added);
	}
}
