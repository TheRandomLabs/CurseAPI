package com.therandomlabs.curseapi.file;

import com.google.gson.annotations.SerializedName;

public enum FileStatus {
	@SerializedName("Normal")
	NORMAL("Normal"),
	@SerializedName("SemiNormal")
	SEMI_NORMAL("SemiNormal"),
	@SerializedName("Reported")
	REPORTED("Reported"),
	@SerializedName("Malformed")
	MALFORMED("Malformed"),
	@SerializedName("Locked")
	LOCKED("Locked"),
	@SerializedName("InvalidLayout")
	INVALID_LAYOUT("InvalidLayout"),
	@SerializedName("Hidden")
	HIDDEN("Hidden"),
	@SerializedName("NeedsApproval")
	NEEDS_APPROVAL("NeedsApproval"),
	@SerializedName("Deleted")
	DELETED("Deleted"),
	@SerializedName("UnderReview")
	UNDER_REVIEW("UnderReview"),
	@SerializedName("MalwareDetected")
	MALWARE_DETECTED("MalwareDetected"),
	@SerializedName("WaitingOnProject")
	WAITING_ON_PROJECT("WaitingOnProject"),
	@SerializedName("ClientOnly")
	CLIENT_ONLY("ClientOnly"),
	@SerializedName("Unknown")
	UNKNOWN("Unknown");

	private final String name;

	FileStatus(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static FileStatus fromName(String name) {
		for(FileStatus status : values()) {
			if(status.name.equalsIgnoreCase(name)) {
				return status;
			}
		}

		return UNKNOWN;
	}
}
