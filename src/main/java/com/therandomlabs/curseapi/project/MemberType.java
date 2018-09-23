package com.therandomlabs.curseapi.project;

import com.google.gson.annotations.SerializedName;

public enum MemberType {
	@SerializedName("Owner")
	OWNER("Owner"),
	@SerializedName("Translator")
	TRANSLATOR("Translator"),
	@SerializedName("Maintainer")
	MAINTAINER("Maintainer"),
	@SerializedName("Contributor")
	CONTRIBUTOR("Contributor"),
	@SerializedName("AddOnAuthor")
	AUTHOR("AddOnAuthor"),
	@SerializedName("Former AddOnAuthor")
	FORMER_AUTHOR("Former AddOnAuthor"),
	@SerializedName("Ticket Manager")
	TICKET_MANAGER("Ticket Manager"),
	@SerializedName("Tester")
	TESTER("Tester"),
	@SerializedName("Artist")
	ARTIST("Artist"),
	@SerializedName("Mascot")
	MASCOT("Mascot"),
	@SerializedName("Unknown")
	UNKNOWN("Unknown");

	private final String name;

	MemberType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static MemberType fromName(String name) {
		for(MemberType type : values()) {
			if(type.toString().equalsIgnoreCase(name)) {
				return type;
			}
		}

		return null;
	}
}
