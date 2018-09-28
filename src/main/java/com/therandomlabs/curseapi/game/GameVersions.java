package com.therandomlabs.curseapi.game;

import java.util.HashSet;
import java.util.Set;
import com.therandomlabs.utils.collection.TRLList;

public final class GameVersions {
	public static final GameVersion UNKNOWN = new GameVersion() {
		@Override
		public String toString() {
			return "Unknown Version";
		}

		@Override
		public String id() {
			return "unknown";
		}

		@Override
		public int compareTo(Object version) {
			return -1;
		}
	};

	public static final GameVersionHandler UNKNOWN_HANDLER = new GameVersionHandler() {
		@Override
		public Game getGame() {
			return Game.UNKNOWN;
		}

		@Override
		public TRLList<GameVersion> getVersions() {
			return new TRLList<>(UNKNOWN);
		}

		@Override
		public TRLList<GameVersionGroup> getGroups() {
			return null;
		}
	};

	private static final Set<GameVersionHandler> handlers = new HashSet<>();

	private GameVersions() {}

	public static void registerHandler(GameVersionHandler handler) {
		handlers.add(handler);
	}

	public static void unregisterHandler(GameVersionHandler handler) {
		handlers.remove(handler);
	}

	static GameVersionHandler getHandler(Game game) {
		for(GameVersionHandler handler : handlers) {
			if(handler.getGame() == game) {
				return handler;
			}
		}

		return UNKNOWN_HANDLER;
	}
}
