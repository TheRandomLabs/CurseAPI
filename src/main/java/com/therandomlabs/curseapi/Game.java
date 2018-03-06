package com.therandomlabs.curseapi;

import com.google.gson.annotations.SerializedName;

/**
 * An {@code enum} containing all of the games supported by Curse.
 *
 * @author TheRandomLabs
 */
public enum Game {
	@SerializedName("Minecraft")
	MINECRAFT("Minecraft"),
	@SerializedName("World of Warcraft")
	WORLD_OF_WARCRAFT("World of Warcraft"),
	@SerializedName("StarCraft II")
	STARCRAFT_II("StarCraft II"),
	@SerializedName("Kerbal Space Program")
	KERBAL_SPACE_PROGRAM("Kerbal Space Program"),
	@SerializedName("WildStar")
	WILDSTAR("WildStar"),
	@SerializedName("Terraria")
	TERRARIA("Terraria"),
	@SerializedName("World of Tanks")
	WORLD_OF_TANKS("World of Tanks"),
	@SerializedName("Rift")
	RIFT("Rift"),
	@SerializedName("Runes of Magic")
	RUNES_OF_MAGIC("Runes of Magic"),
	@SerializedName("Elder Scrolls V: Skyrim")
	SKYRIM("Elder Scrolls V: Skyrim"),
	@SerializedName("The Secret World")
	THE_SECRET_WORLD("The Secret World"),
	@SerializedName("The Elder Scrolls Online")
	THE_ELDER_SCROLLS("The Elder Scrolls Online"),
	@SerializedName("Secret World Legends")
	SECRET_WORLD_LEGENDS("Secret World Legends"),
	@SerializedName("Darkest Dungeon")
	DARKEST_DUNGEON("Darkest Dungeon");

	private final String name;

	Game(String name) {
		this.name = name;
	}

	public static Game fromName(String name) {
		for(Game game : values()) {
			if(game.name.equalsIgnoreCase(name)) {
				return game;
			}
		}
		return null;
	}

	/**
	 * Returns the name of this game.
	 *
	 * @return the name of this game.
	 */
	@Override
	public String toString() {
		return name;
	}
}
