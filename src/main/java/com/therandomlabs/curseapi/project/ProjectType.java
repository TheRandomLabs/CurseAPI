package com.therandomlabs.curseapi.project;

import java.util.Collection;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import com.therandomlabs.curseapi.Game;
import com.therandomlabs.curseapi.curseforge.CurseForgeSite;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.misc.StringUtils;

//Use .equals to compare a type (e.g. modpacks)
//Use == to compare a type and a site (e.g. FTB modpacks)
public final class ProjectType {
	private static final List<ProjectType> values = new TRLList<>();

	static {
		//Initialize all project types
		new Bukkit();
		new Minecraft();
		new KerbalSpaceProgram();
		new WildStar();
		new WorldOfTanks();
		new Terraria();
		new Rift();
		new RunesOfMagic();
		new Skyrim();
		new TheSecretWorld();
		new TheElderScrollsOnline();
		new SecretWorldLegends();
		new DarkestDungeon();
		new SurvivingMars();
		new StardewValley();
		new Staxel();
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

	public CurseForgeSite site() {
		return site;
	}

	public String sitePath() {
		return sitePath;
	}

	@Override
	public String toString() {
		return name;
	}

	public String fullName() {
		return game() + " " + name;
	}

	public Game game() {
		return site.game();
	}

	public String fullSingularName() {
		return game() + " " + singularName();
	}

	public String singularName() {
		return StringUtils.removeLastChar(name);
	}

	public static final class Bukkit {
		@SerializedName("Bukkit Plugins")
		public static final ProjectType BUKKIT_PLUGINS = get("Bukkit Plugins", "projects");

		private Bukkit() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.BUKKIT);
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

		private Minecraft() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.MINECRAFT);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.MINECRAFT, sitePath);
		}
	}

	public static final class FeedTheBeast {
		@SerializedName("Modpacks")
		public static final ProjectType MODPACKS = get("Modpacks", "modpacks");

		private FeedTheBeast() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.FEED_THE_BEAST);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.FEED_THE_BEAST, sitePath);
		}
	}

	public static final class WowAce {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons");

		private WowAce() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.WOW_ACE);
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

		private SCIIMapster() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.SC_II_MAPSTER);
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

		private KerbalSpaceProgram() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.KERBAL_SPACE_PROGRAM);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.KERBAL_SPACE_PROGRAM, sitePath);
		}
	}

	public static final class WildStar {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "ws-addons");

		private WildStar() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.WILDSTAR);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.WILDSTAR, sitePath);
		}
	}

	public static final class WorldOfTanks {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "wot-mods");
		@SerializedName("Skins")
		public static final ProjectType SKINS = get("Skins", "wot-skins");

		private WorldOfTanks() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.WORLD_OF_TANKS);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.WORLD_OF_TANKS, sitePath);
		}
	}

	public static final class Terraria {
		@SerializedName("Maps")
		public static final ProjectType MAPS = get("Maps", "maps");

		private Terraria() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.TERRARIA);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.TERRARIA, sitePath);
		}
	}

	public static final class Rift {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons");

		private Rift() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.RIFT);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.RIFT, sitePath);
		}
	}

	public static final class RunesOfMagic {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons");

		private RunesOfMagic() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.RUNES_OF_MAGIC);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.RUNES_OF_MAGIC, sitePath);
		}
	}

	public static final class Skyrim {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "mods");

		private Skyrim() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.SKYRIM);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.SKYRIM, sitePath);
		}
	}

	public static final class TheSecretWorld {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "tsw-mods");

		private TheSecretWorld() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.THE_SECRET_WORLD);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.THE_SECRET_WORLD, sitePath);
		}
	}

	public static final class TheElderScrollsOnline {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "teso-addons");

		private TheElderScrollsOnline() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.THE_ELDER_SCROLLS_ONLINE);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.THE_ELDER_SCROLLS_ONLINE, sitePath);
		}
	}

	public static final class SecretWorldLegends {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "tswl-mods");

		private SecretWorldLegends() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.SECRET_WORLD_LEGENDS);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.SECRET_WORLD_LEGENDS, sitePath);
		}
	}

	public static final class DarkestDungeon {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "dd-mods");

		private DarkestDungeon() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.DARKEST_DUNGEON);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.DARKEST_DUNGEON, sitePath);
		}
	}

	public static final class SurvivingMars {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "mods");

		private SurvivingMars() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.SURVIVING_MARS);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.SURVIVING_MARS, sitePath);
		}
	}

	public static final class StardewValley {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "mods");

		private StardewValley() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.STARDEW_VALLEY);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.STARDEW_VALLEY, sitePath);
		}
	}

	public static final class Staxel {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "staxel-mods");

		private Staxel() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.STAXEL);
		}

		private static ProjectType get(String name, String sitePath) {
			return new ProjectType(name, CurseForgeSite.STAXEL, sitePath);
		}
	}
}
