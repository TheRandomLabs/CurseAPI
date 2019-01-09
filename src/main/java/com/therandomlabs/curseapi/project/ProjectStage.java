package com.therandomlabs.curseapi.project;

import com.google.gson.annotations.SerializedName;

public enum ProjectStage {
	@SerializedName("Normal")
	NORMAL("Normal"),
	@SerializedName("Hidden")
	HIDDEN("Hidden"),
	@SerializedName("Deleted")
	DELETED("Deleted"),
	@SerializedName("Inactive")
	INACTIVE("Inactive"),
	@SerializedName("Mature")
	MATURE("Mature"),
	@SerializedName("Planning")
	PLANNING("Planning"),
	@SerializedName("Release")
	RELEASE("Release"),
	@SerializedName("Abandoned")
	ABANDONED("Abandoned"),
	@SerializedName("Unknown")
	UNKNOWN("Unknown");

	private final String name;

	ProjectStage(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static ProjectStage fromName(String name) {
		for(ProjectStage stage : values()) {
			if(stage.name.equalsIgnoreCase(name)) {
				return stage;
			}
		}

		return UNKNOWN;
	}
}
