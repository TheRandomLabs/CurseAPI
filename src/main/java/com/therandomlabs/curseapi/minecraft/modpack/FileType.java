package com.therandomlabs.curseapi.minecraft.modpack;

import com.google.gson.annotations.SerializedName;

public enum FileType {
	@SerializedName("normal")
	NORMAL("normal"),
	@SerializedName("clientOnly")
	CLIENT_ONLY("clientOnly"),
	@SerializedName("serverOnly")
	SERVER_ONLY("serverOnly");

	private final String name;

	FileType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static FileType fromBooleans(boolean clientOnly, boolean serverOnly) {
		if(clientOnly && serverOnly) {
			return null;
		}
		if(clientOnly) {
			return CLIENT_ONLY;
		}
		if(serverOnly) {
			return SERVER_ONLY;
		}
		return NORMAL;
	}
}
