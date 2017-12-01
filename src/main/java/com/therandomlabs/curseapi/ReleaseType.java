package com.therandomlabs.curseapi;

import java.util.Locale;
import com.google.gson.annotations.SerializedName;

/**
 * An {@code enum} containing Curse file release types.
 * @author TheRandomLabs
 */
public enum ReleaseType {
	@SerializedName("release")
	RELEASE,
	@SerializedName("beta")
	BETA,
	@SerializedName("alpha")
	ALPHA;

	/**
	 * Returns a string representation of this release type.
	 * @return a string representation of this release type.
	 */
	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Returns the {@link ReleaseType} with the specified name.
	 * @param name a release type name.
	 * @return the {@link ReleaseType} with the specified name,
	 * or {@code null} if it does not exist.
	 */
	public static ReleaseType fromName(String name) {
		for(ReleaseType type : values()) {
			if(type.toString().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
