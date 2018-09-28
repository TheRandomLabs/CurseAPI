package com.therandomlabs.curseapi.game;

import java.util.Collection;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.TRLList;

public interface GameVersionHandler {
	Game getGame();

	TRLList<GameVersion> getVersions();

	TRLList<GameVersionGroup> getGroups();

	default GameVersion get(String id) {
		for(GameVersion version : getVersions()) {
			if(version.id().equalsIgnoreCase(id) || version.toString().equalsIgnoreCase(id)) {
				return version;
			}
		}

		return GameVersions.UNKNOWN;
	}

	default TRLList<GameVersion> get(Collection<String> ids) {
		final TRLList<GameVersion> versions =
				CollectionUtils.map(new TRLList<>(ids.size()), ids, this::get);
		versions.remove(GameVersions.UNKNOWN);
		return versions;
	}
}
