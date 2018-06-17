package com.therandomlabs.curseapi.project;

import com.google.gson.annotations.SerializedName;

/**
 * An {@code enum} containing values to represent the Curse project member types.
 *
 * @author TheRandomLabs
 */
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
	//Apparently it's misspelt
	@SerializedName("Ticket Manger")
	TICKET_MANAGER("Ticket Manger"),
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

	/**
	 * Returns the {@link MemberType} with the specified name.
	 *
	 * @param name a release type name.
	 * @return the {@link MemberType} with the specified name,
	 * or {@code null} if it does not exist.
	 */
	public static MemberType fromName(String name) {
		for(MemberType type : values()) {
			if(type.toString().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * Returns a string representation of this member type.
	 *
	 * @return a string representation of this member type.
	 */
	@Override
	public String toString() {
		return name;
	}
}
