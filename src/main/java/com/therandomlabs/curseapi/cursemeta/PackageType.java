package com.therandomlabs.curseapi.cursemeta;

import com.google.gson.annotations.SerializedName;

public enum PackageType {
	@SerializedName("Folder")
	FOLDER("Folder"),
	@SerializedName("Ctop")
	CTOP("Ctop"),
	@SerializedName("SingleFile")
	SINGLE_FILE("SingleFile"),
	@SerializedName("Cmod2")
	CMOD2("Cmod2"),
	@SerializedName("ModPack")
	MODPACK("ModPack"),
	@SerializedName("Mod")
	MOD("Mod"),
	@SerializedName("Any")
	ANY("Any");

	private final String name;

	PackageType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static PackageType fromName(String name) {
		for(PackageType type : values()) {
			if(type.name.equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
