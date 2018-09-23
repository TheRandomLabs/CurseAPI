package com.therandomlabs.curseapi.file;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import com.therandomlabs.curseapi.minecraft.MinecraftVersion;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.misc.Assertions;

public class FilePredicate implements Predicate<CurseFile> {
	private final HashSet<String> gameVersions = new HashSet<>(2);
	private final HashSet<IntPredicate> idConditions = new HashSet<>(1);
	private final HashSet<Predicate<CurseFile>> conditions = new HashSet<>(1);
	private ReleaseType minimumStability = ReleaseType.ALPHA;

	public FilePredicate(String... gameVersions) {
		withGameVersions(gameVersions);
	}

	public FilePredicate(Collection<String> gameVersions) {
		withGameVersions(gameVersions);
	}


	public FilePredicate(ReleaseType minimumStability, String... gameVersions) {
		withMinimumStability(minimumStability);
		withGameVersions(gameVersions);
	}

	public FilePredicate(ReleaseType minimumStability, Collection<String> gameVersions) {
		withMinimumStability(minimumStability);
		withGameVersions(gameVersions);
	}

	@Override
	public boolean test(CurseFile file) {
		if(!file.gameVersions().containsAny(gameVersions)) {
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

	public FilePredicate withGameVersions(String... versions) {
		withGameVersions(new ImmutableList<>(versions));
		return this;
	}

	public FilePredicate withGameVersions(Collection<String> versions) {
		versions.forEach(version -> Assertions.nonNull(version, "version"));
		gameVersions.addAll(versions);
		return this;
	}

	public FilePredicate withMCVersions(MinecraftVersion... versions) {
		for(MinecraftVersion version : versions) {
			Assertions.nonNull(version, "version");
		}

		gameVersions.addAll(CollectionUtils.toStrings(MinecraftVersion.getVersions(versions)));
		return this;
	}

	public FilePredicate withMCVersions(Collection<MinecraftVersion> versions) {
		withMCVersions(versions.toArray(new MinecraftVersion[0]));
		return this;
	}

	public FilePredicate withMCVersionGroup(String version) {
		withMCVersions(MinecraftVersion.groupFromString(version).getVersions());
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

	public static FilePredicate mc(MinecraftVersion... mcVersions) {
		return new FilePredicate().withMCVersions(mcVersions);
	}

	public static FilePredicate mc(Collection<MinecraftVersion> mcVersions) {
		return new FilePredicate().withMCVersions(mcVersions);
	}

	public static FilePredicate mc(ReleaseType minimumStability, MinecraftVersion... mcVersions) {
		return new FilePredicate().withMinimumStability(minimumStability).
				withMCVersions(mcVersions);
	}

	public static FilePredicate mc(ReleaseType minimumStability,
			Collection<MinecraftVersion> mcVersions) {
		return new FilePredicate().withMinimumStability(minimumStability).
				withMCVersions(mcVersions);
	}
}
