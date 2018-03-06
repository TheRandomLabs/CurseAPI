package com.therandomlabs.curseapi.file;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLCollectors;
import com.therandomlabs.utils.collection.TRLList;

public class CurseFileList extends TRLList<CurseFile> {
	private static final long serialVersionUID = 8733576650037056459L;

	public static final CurseFileList EMPTY = new CurseFileList();

	private CurseFileList(Collection<? extends CurseFile> files) {
		super(files);
	}

	private CurseFileList(CurseFile... files) {
		super(files);
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

	public void newerThan(CurseFile oldFile) {
		filter(file -> file.id() > oldFile.id());
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
		filter(file -> file.id() > oldFile.id() && file.id() <= newFile.id());
	}

	public void filterMCVersionGroup(String version) {
		filterVersions(MinecraftVersion.groupFromString(version));
	}

	public void filterVersions(MinecraftVersion... versions) {
		final Set<MinecraftVersion> versionSet = new HashSet<>();

		for(MinecraftVersion version : versions) {
			if(version.isGroup()) {
				versionSet.addAll(version.getVersions());
			} else {
				versionSet.add(version);
			}
		}

		filterVersions(CollectionUtils.stringify(versionSet));
	}

	public void filterVersions(String... versions) {
		filterVersions(new ImmutableList<>(versions));
	}

	public void filterVersions(Collection<String> versions) {
		filter(file -> file.gameVersions().containsAny(versions));
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

	public void filter(Predicate<? super CurseFile> predicate) {
		removeIf(file -> !predicate.test(file));
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
	public CurseFile[] toArray() {
		return toArray(new CurseFile[0]);
	}

	@Override
	public CurseFileList clone() {
		return new CurseFileList(toArray());
	}

	public static Collector<CurseFile, ?, CurseFileList> toCurseFileList() {
		return TRLCollectors.toCollection(CurseFileList::new);
	}

	public static CurseFileList of(Collection<? extends CurseFile> files) {
		final CurseFileList list = ofUnsorted(files);
		list.sortByNewest();
		return list;
	}

	public static CurseFileList of(CurseFile... files) {
		final CurseFileList list = ofUnsorted(files);
		list.sortByNewest();
		return list;
	}

	public static CurseFileList ofUnsorted(Collection<? extends CurseFile> files) {
		return files.isEmpty() ? EMPTY : new CurseFileList(filter(files));
	}

	public static CurseFileList ofUnsorted(CurseFile... files) {
		return files.length == 0 ?
				EMPTY : new CurseFileList(filter(new ImmutableList<>(files)));
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
}
