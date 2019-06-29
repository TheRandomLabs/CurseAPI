package com.therandomlabs.curseapi.file;

import java.util.Locale;
import com.google.gson.annotations.SerializedName;
import com.therandomlabs.utils.misc.StringUtils;

public enum ReleaseType {
	@SerializedName("release")
	RELEASE,
	@SerializedName("beta")
	BETA,
	@SerializedName("alpha")
	ALPHA,
	@SerializedName("unknown")
	UNKNOWN;

	private final String lowerCase;
	private final String friendlyName;

	ReleaseType() {
		lowerCase = super.toString().toLowerCase(Locale.ENGLISH);
		friendlyName = StringUtils.capitalize(lowerCase, 0);
	}

	@Override
	public String toString() {
		return lowerCase;
	}

	public String friendlyName() {
		return friendlyName;
	}

	public boolean matchesMinimumStability(ReleaseType releaseType) {
		return ordinal() <= releaseType.ordinal();
	}

	public static ReleaseType fromName(String name) {
		if(name.length() == 0) {
			final char initial = Character.toLowerCase(name.charAt(0));

			for(ReleaseType type : values()) {
				if(type.toString().charAt(0) == initial) {
					return type;
				}
			}

			return UNKNOWN;
		}

		for(ReleaseType type : values()) {
			if(type.toString().equalsIgnoreCase(name)) {
				return type;
			}
		}

		return UNKNOWN;
	}
}
