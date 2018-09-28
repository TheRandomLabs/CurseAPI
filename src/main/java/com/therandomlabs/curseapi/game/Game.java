package com.therandomlabs.curseapi.game;

import com.google.gson.annotations.SerializedName;

public enum Game {
	@SerializedName("Minecraft")
	MINECRAFT("Minecraft", 432),
	@SerializedName("World of Warcraft")
	WORLD_OF_WARCRAFT("World of Warcraft", 1),
	@SerializedName("StarCraft II")
	STARCRAFT_II("StarCraft II", 65),
	@SerializedName("Kerbal Space Program")
	KERBAL_SPACE_PROGRAM("Kerbal Space Program", 4401),
	@SerializedName("WildStar")
	WILDSTAR("WildStar", 454),
	@SerializedName("World of Tanks")
	WORLD_OF_TANKS("World of Tanks", 423),
	@SerializedName("Terraria")
	TERRARIA("Terraria", 431),
	@SerializedName("Rift")
	RIFT("Rift", 424),
	@SerializedName("Runes of Magic")
	RUNES_OF_MAGIC("Runes of Magic", 335),
	@SerializedName("Elder Scrolls V: Skyrim")
	SKYRIM("Elder Scrolls V: Skyrim", 449),
	@SerializedName("The Secret World")
	THE_SECRET_WORLD("The Secret World", 64),
	@SerializedName("The Elder Scrolls Online")
	THE_ELDER_SCROLLS("The Elder Scrolls Online", 455),
	@SerializedName("Secret World Legends")
	SECRET_WORLD_LEGENDS("Secret World Legends", 4455),
	@SerializedName("Darkest Dungeon")
	DARKEST_DUNGEON("Darkest Dungeon", 608),
	@SerializedName("Surviving Mars")
	SURVIVING_MARS("Surviving Mars", 61489),
	@SerializedName("Grand Theft Auto V")
	GRAND_THEFT_AUTO_V("Grand Theft Auto V", 496),
	@SerializedName("Stardew Valley")
	STARDEW_VALLEY("Stardew Valley", 669),
	@SerializedName("Staxel")
	STAXEL("Staxel", 18237),
	@SerializedName("Unknown")
	UNKNOWN("Unknown", 0);

	private final String name;
	private final int id;

	Game(String name, int id) {
		this.name = name;
		this.id = id;
	}

	@Override
	public String toString() {
		return name;
	}

	public GameVersionHandler versionHandler() {
		return GameVersions.getHandler(this);
	}

	public static Game fromName(String name) {
		for(Game game : values()) {
			if(game.name.equalsIgnoreCase(name)) {
				return game;
			}
		}

		return UNKNOWN;
	}

	public static Game fromID(int id) {
		for(Game game : values()) {
			if(game.id == id) {
				return game;
			}
		}

		return UNKNOWN;
	}
}
