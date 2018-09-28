package com.therandomlabs.curseapi.game;

import java.util.Collection;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.TRLList;

public interface GameVersionHandler<V extends GameVersion<V, G>, G extends GameVersionGroup<V, G>> {
	Game getGame();

	V getUnknownVersion();

	TRLList<V> getVersions();

	TRLList<G> getGroups();

	default V get(String id) {
		for(V version : getVersions()) {
			if(version.id().equalsIgnoreCase(id) || version.toString().equalsIgnoreCase(id)) {
				return version;
			}
		}

		return getUnknownVersion();
	}

	default TRLList<V> get(Collection<String> ids) {
		final TRLList<V> versions = CollectionUtils.map(new TRLList<>(ids.size()), ids, this::get);
		versions.remove(getUnknownVersion());
		return versions;
	}
}
