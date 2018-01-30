package com.therandomlabs.curseapi;

import java.util.Collection;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import com.therandomlabs.curseapi.curseforge.CurseForgeSite;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;

//Use .equals to compare a type (e.g. modpacks)
//Use == to compare a type and a site (e.g. FTB modpacks)
public final class ProjectType {
	public static final class Bukkit {
		@SerializedName("Bukkit Plugins")
		public static final ProjectType BUKKIT_PLUGINS = get("Bukkit Plugins", "projects");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.BUKKIT, sitePath, values);
		}
	}

	public static final class Minecraft {
		@SerializedName("Modpacks")
		public static final ProjectType MODPACKS = get("Modpacks", "modpacks");
		@SerializedName("Customization")
		public static final ProjectType CUSTOMIZATION = get("Customization", "customization");
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "mc-addons");
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "mc-mods");
		@SerializedName("Texture Packs")
		public static final ProjectType TEXTURE_PACKS = get("Texture Packs", "texture-packs");
		@SerializedName("Worlds")
		public static final ProjectType WORLDS = get("Worlds", "worlds");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.MINECRAFT, sitePath, values);
		}
	}

	public static final class FeedTheBeast {
		@SerializedName("Modpacks")
		public static final ProjectType MODPACKS = get("Modpacks", "modpacks");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.FEED_THE_BEAST, sitePath, values);
		}
	}

	public static final class WowAce {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.WOW_ACE, sitePath, values);
		}
	}

	public static final class SCIIMapster {
		@SerializedName("Assets")
		public static final ProjectType ASSETS = get("Assets", "assets");
		@SerializedName("Maps")
		public static final ProjectType MAPS = get("Maps", "maps");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.SC_II_MAPSTER, sitePath, values);
		}
	}

	public static final class KerbalSpaceProgram {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "ksp-mods");
		@SerializedName("Shareables")
		public static final ProjectType SHAREABLES = get("Shareables", "ksp-shareables");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.KERBAL_SPACE_PROGRAM, sitePath, values);
		}
	}

	public static final class WildStar {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "ws-addons");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.WILDSTAR, sitePath, values);
		}
	}

	public static final class Terraria {
		@SerializedName("Maps")
		public static final ProjectType MAPS = get("Maps", "maps");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.TERRARIA, sitePath, values);
		}
	}

	public static final class WorldOfTanks {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "wot-mods");
		@SerializedName("Skins")
		public static final ProjectType SKINS = get("Skins", "wot-skins");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.WORLD_OF_TANKS, sitePath, values);
		}
	}

	public static final class Rift {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.RIFT, sitePath, values);
		}
	}

	public static final class RunesOfMagic {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.RUNES_OF_MAGIC, sitePath, values);
		}
	}

	public static final class Skyrim {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "mods");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.SKYRIM, sitePath, values);
		}
	}

	public static final class TheSecretWorld {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "tsw-mods");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.THE_SECRET_WORLD, sitePath, values);
		}
	}

	public static final class TheElderScrollsOnline {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "teso-addons");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.THE_ELDER_SCROLLS_ONLINE, sitePath,
					values);
		}
	}

	public static final class SecretWorldLegends {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "tswl-mods");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.SECRET_WORLD_LEGENDS, sitePath, values);
		}
	}

	public static final class DarkestDungeon {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "dd-mods");

		private static final List<ProjectType> values = new TRLList<>();

		public static ProjectType[] values() {
			return values.toArray(new ProjectType[0]);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values, name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.DARKEST_DUNGEON, sitePath, values);
		}
	}

	private static final List<ProjectType> values = new TRLList<>();

	final String name;
	private final CurseForgeSite site;
	private final String sitePath;

	ProjectType(String name, CurseForgeSite site, String sitePath,
			Collection<ProjectType> values) {
		this.name = name;
		this.site = site;
		this.sitePath = sitePath;
		values.add(this);
		ProjectType.values.add(this);
	}

	public CurseForgeSite getSite() {
		return site;
	}

	public String getSitePath() {
		return sitePath;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof ProjectType && name.equals(((ProjectType) object).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public static ProjectType[] values() {
		return values.toArray(new ProjectType[0]);
	}

	public static ProjectType[] values(CurseForgeSite site) {
		return values.stream().filter(type -> type.site == site).toArray(ProjectType[]::new);
	}

	public static ProjectType get(CurseForgeSite site, String name) {
		return valueOf(new ImmutableList<>(values(site)), name);
	}

	static ProjectType valueOf(Collection<ProjectType> values, String name) {
		for(ProjectType type : values) {
			if(type.name.equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
