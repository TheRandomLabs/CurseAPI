package com.therandomlabs.curseapi.project;

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

		Bukkit() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.BUKKIT);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.BUKKIT, sitePath);
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

		Minecraft() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.MINECRAFT);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.MINECRAFT, sitePath);
		}
	}

	public static final class FeedTheBeast {
		@SerializedName("Modpacks")
		public static final ProjectType MODPACKS = get("Modpacks", "modpacks");

		FeedTheBeast() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.FEED_THE_BEAST);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.FEED_THE_BEAST, sitePath);
		}
	}

	public static final class WowAce {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons");

		WowAce() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.WOW_ACE);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.WOW_ACE, sitePath);
		}
	}

	public static final class SCIIMapster {
		@SerializedName("Assets")
		public static final ProjectType ASSETS = get("Assets", "assets");
		@SerializedName("Maps")
		public static final ProjectType MAPS = get("Maps", "maps");

		SCIIMapster() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.SC_II_MAPSTER);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.SC_II_MAPSTER, sitePath);
		}
	}

	public static final class KerbalSpaceProgram {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "ksp-mods");
		@SerializedName("Shareables")
		public static final ProjectType SHAREABLES = get("Shareables", "ksp-shareables");

		KerbalSpaceProgram() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.KERBAL_SPACE_PROGRAM);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.KERBAL_SPACE_PROGRAM, sitePath);
		}
	}

	public static final class WildStar {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "ws-addons");

		WildStar() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.WILDSTAR);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.WILDSTAR, sitePath);
		}
	}

	public static final class Terraria {
		@SerializedName("Maps")
		public static final ProjectType MAPS = get("Maps", "maps");

		Terraria() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.TERRARIA);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.TERRARIA, sitePath);
		}
	}

	public static final class WorldOfTanks {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "wot-mods");
		@SerializedName("Skins")
		public static final ProjectType SKINS = get("Skins", "wot-skins");

		WorldOfTanks() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.WORLD_OF_TANKS);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.WORLD_OF_TANKS, sitePath);
		}
	}

	public static final class Rift {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons");

		Rift() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.RIFT);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.RIFT, sitePath);
		}
	}

	public static final class RunesOfMagic {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons");

		RunesOfMagic() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.RUNES_OF_MAGIC);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.RUNES_OF_MAGIC, sitePath);
		}
	}

	public static final class Skyrim {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "mods");

		Skyrim() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.SKYRIM);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.SKYRIM, sitePath);
		}
	}

	public static final class TheSecretWorld {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "tsw-mods");

		TheSecretWorld() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.THE_SECRET_WORLD);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.THE_SECRET_WORLD, sitePath);
		}
	}

	public static final class TheElderScrollsOnline {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "teso-addons");

		TheElderScrollsOnline() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.THE_ELDER_SCROLLS_ONLINE);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.THE_ELDER_SCROLLS_ONLINE, sitePath);
		}
	}

	public static final class SecretWorldLegends {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "tswl-mods");

		SecretWorldLegends() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.SECRET_WORLD_LEGENDS);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.SECRET_WORLD_LEGENDS, sitePath);
		}
	}

	public static final class DarkestDungeon {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "dd-mods");

		DarkestDungeon() {}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.DARKEST_DUNGEON);
		}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.DARKEST_DUNGEON, sitePath);
		}
	}

	private static final List<ProjectType> values = new TRLList<>();

	static {
		//Initialize all project types
		new Bukkit();
		new Minecraft();
		new KerbalSpaceProgram();
		new WildStar();
		new Terraria();
		new WorldOfTanks();
		new Rift();
		new RunesOfMagic();
		new Skyrim();
		new TheSecretWorld();
		new TheElderScrollsOnline();
		new SecretWorldLegends();
		new DarkestDungeon();
	}

	final String name;
	private final CurseForgeSite site;
	private final String sitePath;

	ProjectType(String name, CurseForgeSite site, String sitePath) {
		this.name = name;
		this.site = site;
		this.sitePath = sitePath;
		values.add(this);
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
		return valueOf(values(site), name);
	}

	static ProjectType valueOf(ProjectType[] values, String name) {
		return valueOf(new ImmutableList<>(values), name);
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
