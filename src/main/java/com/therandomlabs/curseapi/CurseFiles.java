package com.therandomlabs.curseapi;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

import com.google.common.base.Preconditions;

public class CurseFiles extends TreeSet<CurseFile> {
	private static final long serialVersionUID = -7609834501394579694L;

	/**
	 * Creates an empty {@link CurseFiles} instance ordered from newest to oldest.
	 */
	public CurseFiles() {
		//Default constructor.
	}

	public CurseFiles(Comparator<? super CurseFile> comparator) {
		super(comparator);
	}

	public CurseFiles(Collection<? extends CurseFile> files) {
		super(files);
	}

	public CurseFiles(
			Collection<? extends CurseFile> files, Comparator<? super CurseFile> comparator
	) {
		super(comparator);
		addAll(files);
	}

	public Optional<CurseFile> fileWithID(Collection<? extends CurseFile> files, int id) {
		Preconditions.checkNotNull(files, "files should not be null");
		Preconditions.checkArgument(id >= 10, "id should not be smaller than 10");

		for (CurseFile file : files) {
			if (id == file.id()) {
				return Optional.of(file);
			}
		}

		return Optional.empty();
	}

	public CurseFiles withComparator(Comparator<? super CurseFile> comparator) {
		return new CurseFiles(this, comparator);
	}
}
