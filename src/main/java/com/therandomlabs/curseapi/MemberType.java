package com.therandomlabs.curseapi;

import com.google.gson.annotations.SerializedName;

/**
 * An {@code enum} containing Curse project member types.
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
	@SerializedName("Author")
	AUTHOR("Author"),
	@SerializedName("Former Author")
	FORMER_AUTHOR("Former Author"),
	//Apparently it's mispelt
	@SerializedName("Ticket Manger")
	TICKET_MANAGER("Ticket Manger"),
	@SerializedName("Tester")
	TESTER("Tester"),
	@SerializedName("Artist")
	ARTIST("Artist"),
	@SerializedName("Mascot")
	MASCOT("Mascot");


	private final String name;

	MemberType(String name) {
		this.name = name;
	}

	/**
	 * Returns a string representation of this member type.
	 * @return a string representation of this member type.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns the {@link MemberType} with the specified name.
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
}
