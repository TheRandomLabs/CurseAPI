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
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLCollectors;
import com.therandomlabs.utils.collection.TRLList;

public class CurseFileList extends TRLList<CurseFile> {
	private static final long serialVersionUID = 8733576650037056459L;

	public CurseFileList() {}

	public CurseFileList(int initialCapacity) {
		super(initialCapacity);
	}

	public CurseFileList(CurseFile... files) {
		this(true, files);
	}

	public CurseFileList(boolean sortByNewest, CurseFile... files) {
		this(new ImmutableList<>(files), sortByNewest);
	}

	public CurseFileList(Collection<? extends CurseFile> files) {
		this(files, true);
	}

	public CurseFileList(Collection<? extends CurseFile> files, boolean sortByNewest) {
		super(filter(files));
		if(sortByNewest) {
			sortByNewest();
		}
	}

	public CurseFile latest() {
		return isEmpty() ? null : get(0);
	}

	public CurseFile latest(Predicate<CurseFile> predicate) {
		CurseFile latest = null;
		for(CurseFile file : this) {
			if((latest == null || file.id() > latest.id()) && predicate.test(file)) {
				latest = file;
			}
		}
		return latest;
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

	public boolean hasFileOlderThan(int id) {
		for(CurseFile file : this) {
			if(file.id() < id) {
				return true;
			}
		}
		return false;
	}

	public boolean hasFileOlderThanOrEqualTo(int id) {
		return hasFileOlderThan(id - 1);
	}

	public boolean hasFileNewerThan(int id) {
		for(CurseFile file : this) {
			if(file.id() > id) {
				return true;
			}
		}
		return false;
	}

	public boolean hasFileNewerThanOrEqualTo(int id) {
		return hasFileNewerThan(id + 1);
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

	public void between(CurseFile oldFile, CurseFile newFile) {
		//Best defaults for changelogs
		between(oldFile, newFile, false, true);
	}

	public void between(CurseFile oldFile, CurseFile newFile,
			boolean includeOlder, boolean includeNewer) {
		between(oldFile.id(), newFile.id());
	}

	public void between(int oldID, int newID) {
		between(oldID, newID, false, true);
	}

	public void between(int oldID, int newID, boolean includeOlder, boolean includeNewer) {
		final int older = includeOlder ? oldID - 1 : oldID;
		final int newer = includeNewer ? oldID + 1 : newID;
		filter(file -> file.id() > older && file.id() < newer);
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

	private static <E extends CurseFile> Collection<E> filter(Collection<E> collection) {
		final List<E> files = new TRLList<>(collection);
		files.removeIf(Objects::isNull);
		final List<E> duplicates = new TRLList<>();

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

	public static Collector<CurseFile, ?, CurseFileList> toCurseFileList() {
		return TRLCollectors.toCollection(CurseFileList::new);
	}
}
