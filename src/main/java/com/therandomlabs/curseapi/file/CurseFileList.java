package com.therandomlabs.curseapi.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collector;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.ImmutableCollectionException;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLCollectors;
import com.therandomlabs.utils.collection.TRLList;

public class CurseFileList extends TRLList<CurseFile> {
	private static final long serialVersionUID = 8733576650037056459L;

	public static final CurseFileList EMPTY = new EmptyCurseFileList();

	private static class EmptyCurseFileList extends CurseFileList {
		private static final long serialVersionUID = -1451778220667862580L;

		EmptyCurseFileList() {
			super();
		}

		@Override
		public boolean addAll(@SuppressWarnings("unchecked") CurseFile... elements) {
			throw new ImmutableCollectionException();
		}

		@Override
		public boolean remove(Object object) {
			throw new ImmutableCollectionException();
		}

		@Override
		public boolean add(CurseFile file) {
			throw new ImmutableCollectionException();
		}

		@Override
		public boolean removeAll(Collection<?> collection) {
			throw new ImmutableCollectionException();
		}

		@Override
		public void add(int index, CurseFile file) {
			throw new ImmutableCollectionException();
		}

		@Override
		public boolean retainAll(Collection<?> collection) {
			throw new ImmutableCollectionException();
		}

		@Override
		public void clear() {
			throw new ImmutableCollectionException();
		}

		@Override
		public boolean addAll(Collection<? extends CurseFile> collection) {
			throw new ImmutableCollectionException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends CurseFile> collection) {
			throw new ImmutableCollectionException();
		}

		@Override
		public CurseFile set(int index, CurseFile file) {
			throw new ImmutableCollectionException();
		}

		@Override
		public CurseFile remove(int index) {
			throw new ImmutableCollectionException();
		}

		@Override
		public boolean removeIf(Predicate<? super CurseFile> filter) {
			throw new ImmutableCollectionException();
		}
	}

	private boolean sortedByNewest;

	private CurseFileList(Collection<? extends CurseFile> files) {
		super(files);
	}

	private CurseFileList() {}

	private CurseFileList(CurseFile... files) {
		super(files);
	}

	public static Collector<CurseFile, ?, CurseFileList> toCurseFileList() {
		return TRLCollectors.toCollection(CurseFileList::new);
	}

	public static CurseFileList of(Collection<? extends CurseFile> files) {
		final CurseFileList list = ofUnsorted(files);
		list.sortByNewest();
		return list;
	}

	public static CurseFileList ofUnsorted(Collection<? extends CurseFile> files) {
		return files.isEmpty() ? EMPTY : new CurseFileList(filter(files));
	}

	public static CurseFileList of(CurseFile... files) {
		final CurseFileList list = ofUnsorted(files);
		list.sortByNewest();
		return list;
	}

	public static CurseFileList ofUnsorted(CurseFile... files) {
		return files.length == 0 ? EMPTY : new CurseFileList(filter(new ImmutableList<>(files)));
	}

	public static CurseFileList newEmpty() {
		return new CurseFileList();
	}

	public CurseFile latest() {
		return isEmpty() ? null : get(0);
	}

	public CurseFile latest(Predicate<CurseFile> predicate) {
		final CurseFileList byNewest;
		if(sortedByNewest) {
			byNewest = this;
		} else {
			byNewest = clone();
			byNewest.sortByNewest();
		}

		for(CurseFile file : byNewest) {
			if(predicate.test(file)) {
				return file;
			}
		}

		return null;
	}

	public CurseFile latest(Collection<String> versions) {
		return latest(file -> file.gameVersions().containsAny(versions));
	}

	public CurseFile latest(String... versions) {
		return latest(new ImmutableList<>(versions));
	}

	public CurseFile latest(MinecraftVersion... versions) {
		return latest(CollectionUtils.stringify(MinecraftVersion.getVersions(versions)));
	}

	public CurseFile latestWithMCVersionGroup(String version) {
		return latest(MinecraftVersion.groupFromString(version));
	}

	public CurseFile fileWithID(int id) {
		for(CurseFile file : this) {
			if(file.id() == id) {
				return file;
			}
		}
		return null;
	}

	public CurseFile fileClosestToID(int id, boolean preferOlder) {
		CurseFile lastFile = null;

		for(CurseFile file : this) {
			if(file.id() == id) {
				return file;
			}

			if(file.id() < id) {
				if(preferOlder) {
					return file;
				}
				return lastFile == null ? file : lastFile;
			}

			lastFile = file;
		}

		return lastFile;
	}

	public void filterNewerThan(CurseFile oldFile) {
		filter(file -> file.id() > oldFile.id());
	}

	public void filter(Predicate<? super CurseFile> predicate) {
		removeIf(file -> !predicate.test(file));
	}

	public void newerThanOrEqualTo(CurseFile oldFile) {
		filter(file -> file.id() >= oldFile.id());
	}

	public void olderThan(CurseFile newFile) {
		filter(file -> file.id() < newFile.id());
	}

	public void olderThanOrEqualTo(CurseFile newFile) {
		filter(file -> file.id() <= newFile.id());
	}

	public void between(int oldID, int newID) {
		filter(file -> file.id() > oldID && file.id() <= newID);
	}

	public void between(CurseFile oldFile, CurseFile newFile) {
		between(oldFile.id(), newFile.id());
	}

	public void filterMCVersionGroup(String version) {
		filterVersions(MinecraftVersion.groupFromString(version));
	}

	public void filterVersions(MinecraftVersion... versions) {
		filterVersions(CollectionUtils.stringify(MinecraftVersion.getVersions(versions)));
	}

	public void filterVersions(Collection<String> versions) {
		filter(file -> file.gameVersions().containsAny(versions));
	}

	public void filterVersions(String... versions) {
		filterVersions(new ImmutableList<>(versions));
	}

	public void filterMinimumStability(ReleaseType type) {
		if(type == ReleaseType.ALPHA) {
			return;
		}

		if(type == ReleaseType.BETA) {
			filterReleaseTypes(ReleaseType.RELEASE, ReleaseType.BETA);
			return;
		}

		filterReleaseTypes(ReleaseType.RELEASE);
	}

	public void filterReleaseTypes(ReleaseType... versions) {
		filterReleaseTypes(new ImmutableList<>(versions));
	}

	public void filterReleaseTypes(Collection<ReleaseType> versions) {
		filter(file -> versions.contains(file.releaseType()));
	}

	private static <E extends CurseFile> Collection<E> filter(Collection<E> collection) {
		final List<E> files = new ArrayList<>(collection);
		files.removeIf(Objects::isNull);
		final List<E> duplicates = new ArrayList<>();

		for(int i = 0; i < files.size(); i++) {
			for(int j = 0; j < files.size(); j++) {
				if(i != j && files.get(i).id() == files.get(j).id()) {
					duplicates.add(files.get(i));
				}
			}
		}

		files.removeAll(duplicates);
		return files;
	}

	public void removeDuplicateProjects() {
		final List<CurseFile> duplicates = new ArrayList<>();

		for(int i = 0; i < size(); i++) {
			for(int j = 0; j < size(); j++) {
				if(i != j && get(i).project().id() == get(j).project().id()) {
					//Prefer newest
					if(get(j).id() > get(i).id()) {
						duplicates.add(get(j));
					} else {
						duplicates.add(get(i));
					}
				}
			}
		}

		removeAll(duplicates);
	}

	public void sortByNewest() {
		sort((file1, file2) -> Integer.compare(file2.id(), file1.id()));
		sortedByNewest = true;
	}

	public boolean isSortedByNewest() {
		return sortedByNewest;
	}

	public void sortByOldest() {
		sort(Comparator.comparingInt(CurseFile::id));
	}

	public void sortByProjectTitle() {
		sort(Comparator.comparing(CurseFile::projectTitle));
	}

	@Override
	public CurseFileList clone() {
		return new CurseFileList(toArray());
	}	@Override
	public void sort(Comparator<? super CurseFile> comparator) {
		super.sort(comparator);
		sortedByNewest = false;
	}

	@Override
	public CurseFile[] toArray() {
		return toArray(new CurseFile[0]);
	}



	@Override
	public boolean add(CurseFile file) {
		if(contains(file)) {
			return false;
		}

		super.add(file);
		return true;
	}

	@Override
	public void add(int index, CurseFile file) {
		if(!contains(file)) {
			super.add(index, file);
		}
	}
}
