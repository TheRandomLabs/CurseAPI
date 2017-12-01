package com.therandomlabs.curseapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.curseapi.minecraft.modpack.ModpackFile;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLCollectors;
import com.therandomlabs.utils.collection.TRLList;

public class CurseFileList extends ImmutableList<CurseFile> {
	private static final long serialVersionUID = 8733576650037056459L;

	public static final CurseFileList EMPTY = new CurseFileList();

	SortType sortType;

	enum SortType {
		NEWEST,
		OLDEST,
		PROJECT_TITLE;
	}

	private CurseFileList(Collection<? extends CurseFile> files) {
		super(files);
	}

	private CurseFileList(CurseFile... files) {
		super(files);
	}

	public CurseFileList filterMCVersionGroup(String version) {
		return filterVersions(MinecraftVersion.groupFromString(version));
	}

	public CurseFileList filterVersions(MinecraftVersion... versions) {
		final Set<MinecraftVersion> versionSet = new HashSet<>();
		for(MinecraftVersion version : versions) {
			if(version.isGroup()) {
				versionSet.addAll(version.getVersions());
			} else {
				versionSet.add(version);
			}
		}
		return filterVersions(CollectionUtils.stringify(versionSet));
	}

	public CurseFileList filterVersions(String... versions) {
		return filterVersions(new ImmutableList<>(versions));
	}

	public CurseFileList filterVersions(Collection<String> versions) {
		return filter(file -> file.gameVersions().containsAny(versions));
	}

	public CurseFileList filterMinimumStability(ReleaseType type) {
		if(type == ReleaseType.ALPHA) {
			return this;
		}
		if(type == ReleaseType.BETA) {
			return filterReleaseTypes(ReleaseType.RELEASE, ReleaseType.BETA);
		}
		return filterReleaseTypes(ReleaseType.RELEASE);
	}

	public CurseFileList filterReleaseTypes(ReleaseType... versions) {
		return filterReleaseTypes(new ImmutableList<>(versions));
	}

	public CurseFileList filterReleaseTypes(Collection<ReleaseType> versions) {
		return filter(file -> versions.contains(file.releaseType()));
	}

	public CurseFileList filter(Predicate<? super CurseFile> predicate) {
		return isEmpty() ? this : stream().filter(predicate).collect(toCurseFileList());
	}

	public CurseFileList filterDuplicateProjects() {
		final List<CurseFile> files = new TRLList<>(toArray());
		final List<CurseFile> duplicates = new ArrayList<>();

		for(int i = 0; i < files.size(); i++) {
			for(int j = 0; j < files.size(); j++) {
				if(i != j && files.get(i).project().id() == files.get(j).project().id()) {
					//Prefer newest
					if(files.get(i).uploadTime().compareTo(files.get(j).uploadTime()) > 0) {
						duplicates.add(files.get(j));
					} else {
						duplicates.add(files.get(i));
					}
				}
			}
		}

		files.removeAll(duplicates);
		return new CurseFileList(files);
	}

	public CurseFileList sortedByNewest() {
		return sorted(SortType.NEWEST,
				(file1, file2) -> file2.uploadTime().compareTo(file1.uploadTime()));
	}

	public CurseFileList sortedByOldest() {
		return sorted(SortType.OLDEST,
				(file1, file2) -> file1.uploadTime().compareTo(file2.uploadTime()));
	}

	public CurseFileList sortedByProjectTitle() {
		return sorted(SortType.PROJECT_TITLE, (file1, file2) ->
				file1.project().title().compareTo(file2.project().title()));
	}

	public CurseFileList sorted(Comparator<? super CurseFile> comparator) {
		return sorted(null, comparator);
	}

	private CurseFileList sorted(SortType type, Comparator<? super CurseFile> comparator) {
		if(isEmpty() || type == sortType) {
			return this;
		}

		final CurseFile[] files = toArray(new CurseFile[0]);
		Arrays.sort(files, comparator);

		final List<CurseFile> fileList = new ArrayList<>(files.length);

		for(int i = 0; i < files.length; i++) {
			CurseFile file = files[i];

			//Prefer ModpackFile if it's a duplicate
			if(i != files.length - 1 && files[i + 1].id() == file.id()) {
				if(!(file instanceof ModpackFile)) {
					file = files[i + 1];
				}
				i++;
			}

			fileList.add(file);
		}

		final CurseFileList list = new CurseFileList(files);
		list.sortType = type;
		return list;
	}

	@Override
	public CurseFile[] toArray() {
		return toArray(new CurseFile[0]);
	}

	public static Collector<CurseFile, ?, CurseFileList> toCurseFileList() {
		return TRLCollectors.toImmutableCollection(CurseFileList::new, false);
	}

	public static CurseFileList of(Collection<? extends CurseFile> files) {
		return ofUnsorted(files).sortedByNewest();
	}

	public static CurseFileList of(CurseFile... files) {
		return ofUnsorted(files).sortedByNewest();
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
		files.removeIf(file -> file == null);
		final List<E> duplicates = new ArrayList<>();

		for(int i = 0; i < files.size(); i++) {
			for(int j = 0; j < files.size(); j++) {
				if(i != j && files.get(i).id() == files.get(j).id()) {
					//Prefer ModpackFile
					if(files.get(i) instanceof ModpackFile) {
						duplicates.add(files.get(j));
					} else {
						duplicates.add(files.get(i));
					}
				}
			}
		}

		files.removeAll(duplicates);
		return files;
	}
}
