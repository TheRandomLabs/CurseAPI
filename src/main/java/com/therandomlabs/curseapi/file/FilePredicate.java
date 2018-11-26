package com.therandomlabs.curseapi.file;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import com.therandomlabs.curseapi.game.GameVersion;
import com.therandomlabs.curseapi.game.GameVersionGroup;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.misc.Assertions;

public class FilePredicate implements Predicate<CurseFile> {
	private final HashSet<String> gameVersions = new HashSet<>(2);
	private final HashSet<IntPredicate> idConditions = new HashSet<>(1);
	private final HashSet<Predicate<CurseFile>> conditions = new HashSet<>(1);

	private ReleaseType minimumStability = ReleaseType.ALPHA;

	@Override
	public boolean test(CurseFile file) {
		if(!gameVersions.isEmpty() && !file.gameVersionStrings().containsAny(gameVersions)) {
			return false;
		}

		final int id = file.id();

		for(IntPredicate condition : idConditions) {
			if(!condition.test(id)) {
				return false;
			}
		}

		for(Predicate<CurseFile> condition : conditions) {
			if(!condition.test(file)) {
				return false;
			}
		}

		return file.matchesMinimumStability(minimumStability);
	}

	public boolean test(int id, ReleaseType releaseType, String... gameVersions) {
		return test(id, releaseType, new ImmutableList<>(gameVersions));
	}

	public boolean test(int id, ReleaseType releaseType, TRLList<String> gameVersions) {
		if(!gameVersions.containsAny(this.gameVersions)) {
			return false;
		}

		if(id != 0) {
			for(IntPredicate condition : idConditions) {
				if(!condition.test(id)) {
					return false;
				}
			}
		}

		return releaseType.matchesMinimumStability(minimumStability);
	}

	@SuppressWarnings("unchecked")
	public Set<String> gameVersions() {
		return (Set<String>) gameVersions.clone();
	}

	@SuppressWarnings("unchecked")
	public Set<IntPredicate> idConditions() {
		return (Set<IntPredicate>) idConditions.clone();
	}

	@SuppressWarnings("unchecked")
	public Set<Predicate<CurseFile>> conditions() {
		return (Set<Predicate<CurseFile>>) conditions.clone();
	}

	public ReleaseType minimumStability() {
		return minimumStability;
	}

	public FilePredicate withGameVersions(GameVersion... versions) {
		withGameVersions(new ImmutableList<>(versions));
		return this;
	}

	public FilePredicate withGameVersions(Collection<GameVersion> versions) {
		for(GameVersion version : versions) {
			Assertions.nonNull(version, "version");
			gameVersions.add(version.toString());
		}

		return this;
	}

	public FilePredicate withGameVersionGroups(GameVersionGroup... groups) {
		withGameVersionGroups(new ImmutableList<>(groups));
		return this;
	}

	public FilePredicate withGameVersionGroups(Collection<GameVersionGroup> groups) {
		groups.forEach(group -> Assertions.nonNull(group, "group"));
		groups.stream().map(GameVersionGroup::getVersions).forEach(this::withGameVersions);
		return this;
	}

	public FilePredicate withGameVersionStrings(String... versions) {
		withGameVersionStrings(new ImmutableList<>(versions));
		return this;
	}

	public FilePredicate withGameVersionStrings(Collection<String> versions) {
		for(String version : versions) {
			Assertions.nonNull(version, "version");
			gameVersions.add(version);
		}

		return this;
	}

	public FilePredicate withMinimumStability(ReleaseType stability) {
		minimumStability = stability == null ? ReleaseType.ALPHA : stability;
		return this;
	}

	public FilePredicate withIDCondition(IntPredicate condition) {
		Assertions.nonNull(condition, "condition");
		idConditions.add(condition);
		return this;
	}

	public FilePredicate withCondition(Predicate<CurseFile> condition) {
		Assertions.nonNull(condition, "condition");
		conditions.add(condition);
		return this;
	}
}
