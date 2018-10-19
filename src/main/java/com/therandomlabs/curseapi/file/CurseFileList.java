package com.therandomlabs.curseapi.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collector;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.cursemeta.AddOnFile;
import com.therandomlabs.curseapi.game.Game;
import com.therandomlabs.curseapi.game.GameVersion;
import com.therandomlabs.curseapi.game.GameVersionGroup;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLCollectors;
import com.therandomlabs.utils.collection.TRLList;

public class CurseFileList extends TRLList<CurseFile> {
	private static final long serialVersionUID = 8733576650037056459L;

	public CurseFileList() {}

	public CurseFileList(int initialCapacity) {
		super(initialCapacity);
	}

	public CurseFileList(int projectID, Collection<AddOnFile> files, Game game)
			throws CurseException {
		this(Collections.singletonMap(projectID, files), game, true);
	}

	public CurseFileList(Map<Integer, Collection<AddOnFile>> files, Game game)
			throws CurseException {
		this(files, game, true);
	}

	public CurseFileList(Map<Integer, Collection<AddOnFile>> files, Game game, boolean sortByNewest)
			throws CurseException {
		this(AddOnFile.toCurseFiles(files, game), sortByNewest);
	}

	public CurseFileList(CurseFile... files) {
		this(true, files);
	}

	public CurseFileList(boolean sortByNewest, CurseFile... files) {
		this(new ImmutableList<>(files), sortByNewest);
	}

	public CurseFileList(Collection<CurseFile> files) {
		this(files, true);
	}

	public CurseFileList(Collection<CurseFile> files, boolean sortByNewest) {
		super(filter(files));

		if(sortByNewest) {
			sortByNewest();
		}
	}

	@Override
	public CurseFileList clone() {
		return (CurseFileList) super.clone();
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

	@Override
	public CurseFile[] toArray() {
		return toArray(new CurseFile[0]);
	}

	public CurseFile latest() {
		return latest(file -> true);
	}

	public CurseFile latest(Predicate<? super CurseFile> predicate) {
		if(isEmpty()) {
			return null;
		}

		CurseFile latest = null;

		for(CurseFile file : this) {
			if((latest == null || file.id() > latest.id()) && predicate.test(file)) {
				latest = file;
			}
		}

		return latest;
	}

	public CurseFile latestWithMinimumStabillity(ReleaseType releaseType) {
		return latest(new FilePredicate().withMinimumStability(releaseType));
	}

	public CurseFile latestWithGameVersion(GameVersion... gameVersions) {
		return latestWithGameVersion(new ImmutableList<>(gameVersions));
	}

	public CurseFile latestWithGameVersion(Collection<GameVersion> gameVersions) {
		return latest(new FilePredicate().withGameVersions(gameVersions));
	}

	public CurseFile latestWithGameVersionGroup(GameVersionGroup... gameVersionGroups) {
		return latestWithGameVersionGroup(new ImmutableList<>(gameVersionGroups));
	}

	public CurseFile latestWithGameVersionGroup(Collection<GameVersionGroup> gameVersionGroups) {
		return latest(new FilePredicate().withGameVersionGroups(gameVersionGroups));
	}

	public CurseFile latestWithGameVersionString(String... gameVersionStrings) {
		return latestWithGameVersionString(new ImmutableList<>(gameVersionStrings));
	}

	public CurseFile latestWithGameVersionString(Collection<String> gameVersionStrings) {
		return latest(new FilePredicate().withGameVersionStrings(gameVersionStrings));
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
		removeIf(predicate.negate());
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

	public void between(CurseFile oldFile, CurseFile newFile, boolean includeOlder,
			boolean includeNewer) {
		between(oldFile.id(), newFile.id(), includeOlder, includeNewer);
	}

	public void between(int oldID, int newID) {
		between(oldID, newID, false, true);
	}

	public void between(int oldID, int newID, boolean includeOlder, boolean includeNewer) {
		final int older = includeOlder ? oldID - 1 : oldID;
		final int newer = includeNewer ? newID + 1 : newID;

		filter(file -> file.id() > older && file.id() < newer);
	}

	public void filterMinimumStability(ReleaseType releaseType) {
		filter(new FilePredicate().withMinimumStability(releaseType));
	}

	public void filterGameVersions(GameVersion... gameVersions) {
		filterGameVersions(new ImmutableList<>(gameVersions));
	}

	public void filterGameVersions(Collection<GameVersion> gameVersions) {
		filter(new FilePredicate().withGameVersions(gameVersions));
	}

	public void filterGameVersionGroups(GameVersionGroup... gameVersionGroups) {
		filterGameVersionGroups(new ImmutableList<>(gameVersionGroups));
	}

	public void filterGameVersionGroups(Collection<GameVersionGroup> gameVersionGroups) {
		filter(new FilePredicate().withGameVersionGroups(gameVersionGroups));
	}

	public void filterGameVersionStrings(String... gameVersionStrings) {
		filterGameVersionStrings(new ImmutableList<>(gameVersionStrings));
	}

	public void filterGameVersionStrings(Collection<String> gameVersionStrings) {
		filter(new FilePredicate().withGameVersionStrings(gameVersionStrings));
	}

	public void removeDuplicateProjects() {
		final List<CurseFile> duplicates = new ArrayList<>();

		for(int i = 0; i < size(); i++) {
			for(int j = 0; j < size(); j++) {
				if(i != j && get(i).projectID() == get(j).projectID()) {
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

	public static Collector<CurseFile, ?, CurseFileList> collector() {
		return TRLCollectors.toCollection(CurseFileList::new);
	}

	private static <E extends CurseFile> Collection<E> filter(Collection<E> collection) {
		final List<E> files = new TRLList<>(collection);
		final List<E> duplicates = new TRLList<>();

		files.removeIf(Objects::isNull);

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
