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
	ALPHA;

	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}

	public String friendlyName() {
		return StringUtils.capitalizeRegion(toString(), 0, 1);
	}

	public boolean matchesMinimumStability(ReleaseType releaseType) {
		return ordinal() <= releaseType.ordinal();
	}

	public static ReleaseType fromName(String name) {
		for(ReleaseType type : values()) {
			if(type.toString().equalsIgnoreCase(name)) {
				return type;
			}
		}

		return null;
	}
}
