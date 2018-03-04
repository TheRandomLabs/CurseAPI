package com.therandomlabs.curseapi.project;

import com.google.gson.annotations.SerializedName;

public enum RelationType {
	@SerializedName("All Types")
	ALL_TYPES("All Types"),
	@SerializedName("Embedded Library")
	EMBEDDED_LIBRARY("Embedded Library"),
	@SerializedName("Optional Library")
	OPTIONAL_LIBRARY("Optional Library"),
	@SerializedName("Required Library")
	REQUIRED_LIBRARY("Required Library"),
	@SerializedName("Tool")
	TOOL("Tool"),
	@SerializedName("Incompatible")
	INCOMPATIBLE("Incompatible"),
	@SerializedName("Include")
	INCLUDE("Include");

	private final String name;

	RelationType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
