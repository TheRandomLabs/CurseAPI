package com.therandomlabs.curseapi.minecraft;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.google.gson.annotations.SerializedName;
import com.therandomlabs.utils.collection.TRLList;

/**
 * An {@code enum} containing all Minecraft versions listed on Curse.
 *
 * @author TheRandomLabs
 */
public enum MinecraftVersion {
	@SerializedName("1.13-Group")
	V1_13_GROUP,
	@SerializedName("1.13-Snapshot")
	V1_13_SNAPSHOT(V1_13_GROUP),
	@SerializedName("1.12-Group")
	V1_12_GROUP,
	@SerializedName("1.12.2")
	V1_12_2(V1_12_GROUP),
	@SerializedName("1.12.1")
	V1_12_1(V1_12_GROUP),
	@SerializedName("1.12")
	V1_12(V1_12_GROUP),
	@SerializedName("1.12-Snapshot")
	V1_12_SNAPSHOT(V1_12_GROUP),
	@SerializedName("1.11-Group")
	V1_11_GROUP,
	@SerializedName("1.11.2")
	V1_11_2(V1_11_GROUP),
	@SerializedName("1.11.1")
	V1_11_1(V1_11_GROUP),
	@SerializedName("1.11")
	V1_11(V1_11_GROUP),
	@SerializedName("1.11-Snapshot")
	V1_11_SNAPSHOT(V1_11_GROUP),
	@SerializedName("1.10-Group")
	V1_10_GROUP,
	@SerializedName("1.10.2")
	V1_10_2(V1_10_GROUP),
	@SerializedName("1.10.1")
	V1_10_1(V1_10_GROUP),
	@SerializedName("1.10")
	V1_10(V1_10_GROUP),
	@SerializedName("1.10-Snapshot")
	V1_10_SNAPSHOT(V1_10_GROUP),
	@SerializedName("1.9-Group")
	V1_9_GROUP,
	@SerializedName("1.9.4")
	V1_9_4(V1_9_GROUP),
	@SerializedName("1.9.3")
	V1_9_3(V1_9_GROUP),
	@SerializedName("1.9.2")
	V1_9_2(V1_9_GROUP),
	@SerializedName("1.9.1")
	V1_9_1(V1_9_GROUP),
	@SerializedName("1.9")
	V1_9(V1_9_GROUP),
	@SerializedName("1.9-Snapshot")
	V1_9_SNAPSHOT(V1_9_GROUP),
	@SerializedName("1.8-Group")
	V1_8_GROUP,
	@SerializedName("1.8.9")
	V1_8_9(V1_8_GROUP),
	@SerializedName("1.8.8")
	V1_8_8(V1_8_GROUP),
	@SerializedName("1.8.7")
	V1_8_7(V1_8_GROUP),
	@SerializedName("1.8.6")
	V1_8_6(V1_8_GROUP),
	@SerializedName("1.8.5")
	V1_8_5(V1_8_GROUP),
	@SerializedName("1.8.4")
	V1_8_4(V1_8_GROUP),
	@SerializedName("1.8.3")
	V1_8_3(V1_8_GROUP),
	@SerializedName("1.8.2")
	V1_8_2(V1_8_GROUP),
	@SerializedName("1.8.1")
	V1_8_1(V1_8_GROUP),
	@SerializedName("1.8")
	V1_8(V1_8_GROUP),
	@SerializedName("1.8-Snapshot")
	V1_8_SNAPSHOT(V1_8_GROUP),
	@SerializedName("1.7-Group")
	V1_7_GROUP,
	@SerializedName("1.7.10")
	V1_7_10(V1_7_GROUP),
	@SerializedName("1.7.9")
	V1_7_9(V1_7_GROUP),
	@SerializedName("1.7.8")
	V1_7_8(V1_7_GROUP),
	@SerializedName("1.7.7")
	V1_7_7(V1_7_GROUP),
	@SerializedName("1.7.6")
	V1_7_6(V1_7_GROUP),
	@SerializedName("1.7.5")
	V1_7_5(V1_7_GROUP),
	@SerializedName("1.7.4")
	V1_7_4(V1_7_GROUP),
	@SerializedName("1.7.2")
	V1_7_2(V1_7_GROUP),
	@SerializedName("1.6-Group")
	V1_6_GROUP,
	@SerializedName("1.6.4")
	V1_6_4(V1_6_GROUP),
	@SerializedName("1.6.2")
	V1_6_2(V1_6_GROUP),
	@SerializedName("1.6.1")
	V1_6_1(V1_6_GROUP),
	@SerializedName("1.5-Group")
	V1_5_GROUP,
	@SerializedName("1.5.2")
	V1_5_2(V1_5_GROUP),
	@SerializedName("1.5.1")
	V1_5_1(V1_5_GROUP),
	@SerializedName("1.5.0")
	V1_5_0(V1_5_GROUP),
	@SerializedName("1.4-Group")
	V1_4_GROUP,
	@SerializedName("1.4.7")
	V1_4_7(V1_4_GROUP),
	@SerializedName("1.4.6")
	V1_4_6(V1_4_GROUP),
	@SerializedName("1.4.5")
	V1_4_5(V1_4_GROUP),
	@SerializedName("1.4.4")
	V1_4_4(V1_4_GROUP),
	@SerializedName("1.4.2")
	V1_4_2(V1_4_GROUP),
	@SerializedName("1.3-Group")
	V1_3_GROUP,
	@SerializedName("1.3.2")
	V1_3_2(V1_3_GROUP),
	@SerializedName("1.3.1")
	V1_3_1(V1_3_GROUP),
	@SerializedName("1.2-Group")
	V1_2_GROUP,
	@SerializedName("1.2.5")
	V1_2_5(V1_2_GROUP),
	@SerializedName("1.2.4")
	V1_2_4(V1_2_GROUP),
	@SerializedName("1.2.3")
	V1_2_3(V1_2_GROUP),
	@SerializedName("1.2.2")
	V1_2_2(V1_2_GROUP),
	@SerializedName("1.2.1")
	V1_2_1(V1_2_GROUP),
	@SerializedName("1.1-Group")
	V1_1_GROUP,
	@SerializedName("1.1")
	V1_1(V1_1_GROUP),
	@SerializedName("1.0-Group")
	V1_0_GROUP,
	@SerializedName("1.0.0")
	V1_0_0(V1_0_GROUP);

	private final String versionString;
	private final TRLList<MinecraftVersion> versions = new TRLList<>(3);
	private final MinecraftVersion group;
	private TRLList<MinecraftVersion> immutableVersions;

	MinecraftVersion(MinecraftVersion group) {
		this.group = group;
		group.versions.add(this);
		versions.add(this);
		this.versionString = super.toString().substring(1).replaceAll("_", ".");
	}

	MinecraftVersion() {
		this.group = this;
		this.versionString = super.toString().substring(1).replaceAll("_", ".").
				replaceAll(".GROUP", "-Group").replaceAll(".SNAPSHOT", "-Snapshot");
	}

	public static MinecraftVersion latest() {
		return values()[1];
	}

	public static MinecraftVersion latestGroup() {
		return values()[0];
	}

	public static MinecraftVersion groupFromString(String version) {
		final MinecraftVersion mcVersion = fromString(version);
		return mcVersion == null ? null : mcVersion.getGroup();
	}

	/**
	 * Returns the {@link MinecraftVersion} with the specified version string.
	 *
	 * @param version a version string.
	 * @return the {@link MinecraftVersion} with the specified version string,
	 * or {@code null} if it does not exist.
	 */
	public static MinecraftVersion fromString(String version) {
		for(MinecraftVersion mcVersion : values()) {
			if(mcVersion.versionString.equalsIgnoreCase(version)) {
				return mcVersion;
			}
		}
		return null;
	}

	public static Set<MinecraftVersion> getVersions(Collection<MinecraftVersion> versions) {
		return getVersions(versions.toArray(new MinecraftVersion[0]));
	}

	public static Set<MinecraftVersion> getVersions(MinecraftVersion... versions) {
		final Set<MinecraftVersion> versionSet = new HashSet<>();

		for(MinecraftVersion version : versions) {
			if(version.isGroup()) {
				versionSet.addAll(version.getVersions());
			} else {
				versionSet.add(version);
			}
		}

		return versionSet;
	}

	public MinecraftVersion getGroup() {
		return group;
	}

	public TRLList<MinecraftVersion> getVersions() {
		if(immutableVersions == null) {
			immutableVersions = versions.toImmutableList();
		}
		return immutableVersions;
	}

	public boolean isVersion() {
		return getGroup() != this;
	}

	public boolean isGroup() {
		return getGroup() == this;
	}

	/**
	 * Returns a string representation of this Minecraft version.
	 *
	 * @return a string representation of this Minecraft version.
	 */
	@Override
	public String toString() {
		return versionString;
	}
}
