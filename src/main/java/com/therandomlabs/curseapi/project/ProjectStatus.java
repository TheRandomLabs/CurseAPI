package com.therandomlabs.curseapi.project;

import com.google.gson.annotations.SerializedName;

public enum ProjectStatus {
	@SerializedName("Normal")
	NORMAL("Normal"),
	@SerializedName("Hidden")
	HIDDEN("Hidden"),
	@SerializedName("Deleted")
	DELETED("Deleted"),
	@SerializedName("Unknown")
	UNKNOWN("Unknown");

	private final String name;

	ProjectStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static ProjectStatus fromName(String name) {
		for(ProjectStatus status : values()) {
			if(status.name.equalsIgnoreCase(name)) {
				return status;
			}
		}

		return UNKNOWN;
	}
}
