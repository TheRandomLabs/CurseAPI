package com.therandomlabs.curseapi.project;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import com.google.gson.annotations.SerializedName;
import com.therandomlabs.curseapi.CurseForge;
import com.therandomlabs.curseapi.CurseForgeSite;
import com.therandomlabs.curseapi.Game;
import com.therandomlabs.utils.collection.ImmutableList;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.misc.StringUtils;

//Use .equals to compare two types (e.g. mods)
//Use == to compare a type and a site (e.g. Minecraft mods)
public final class ProjectType {
	public static final class Bukkit {
		@SerializedName("Bukkit Plugins")
		public static final ProjectType PLUGINS =
				get("Bukkit Plugins", "projects", "minecraft/bukkit-plugins");

		private Bukkit() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.BUKKIT);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.BUKKIT, path, mainCurseForgePath);
		}
	}

	public static final class Minecraft {
		@SerializedName("Modpacks")
		public static final ProjectType MODPACKS =
				get("Modpacks", "modpacks", "minecraft/modpacks");
		@SerializedName("Customization")
		public static final ProjectType CUSTOMIZATION =
				get("Customization", "customization", "minecraft/customization");
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "mc-addons", "minecraft/mc-addons");
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "mc-mods", "minecraft/mc-mods");
		@SerializedName("Texture Packs")
		public static final ProjectType TEXTURE_PACKS =
				get("Texture Packs", "texture-packs", "minecraft/texture-packs");
		@SerializedName("Worlds")
		public static final ProjectType WORLDS = get("Worlds", "worlds", "minecraft/worlds");

		private Minecraft() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.MINECRAFT);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.MINECRAFT, path, mainCurseForgePath);
		}
	}

	public static final class FeedTheBeast {
		@SerializedName("Modpacks")
		public static final ProjectType MODPACKS =
				get("Modpacks", "modpacks", "minecraft/modpacks");

		private FeedTheBeast() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.FEED_THE_BEAST);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.FEED_THE_BEAST, path, mainCurseForgePath);
		}
	}

	public static final class WowAce {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons", "wow/addons");

		private WowAce() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.WOW_ACE);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.WOW_ACE, path, mainCurseForgePath);
		}
	}

	public static final class WorldOfWarcraft {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons", "wow/addons");

		private WorldOfWarcraft() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.WORLD_OF_WARCRAFT);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.WORLD_OF_WARCRAFT, path,
					mainCurseForgePath);
		}
	}

	public static final class SCIIMapster {
		@SerializedName("Assets")
		public static final ProjectType ASSETS = get("Assets", "assets", "sc2/assets");
		@SerializedName("Maps")
		public static final ProjectType MAPS = get("Maps", "maps", "sc2/maps");

		private SCIIMapster() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.SC2MAPSTER);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.SC2MAPSTER, path, mainCurseForgePath);
		}
	}

	public static final class KerbalSpaceProgram {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "ksp-mods", "kerbal/ksp-mods");
		@SerializedName("Shareables")
		public static final ProjectType SHAREABLES =
				get("Shareables", "shareables", "kerbal/shareables");
		@SerializedName("Missions")
		public static final ProjectType MISSIONS = get("Missions", "missions", "kerbal/missions");

		private KerbalSpaceProgram() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.KERBAL_SPACE_PROGRAM);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.KERBAL_SPACE_PROGRAM, path,
					mainCurseForgePath);
		}
	}

	public static final class WildStar {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "ws-addons", "wildstar/ws-addons");

		private WildStar() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.WILDSTAR);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.WILDSTAR, path, mainCurseForgePath);
		}
	}

	public static final class WorldOfTanks {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "wot-mods", "worldoftanks/wot-mods");
		@SerializedName("Skins")
		public static final ProjectType SKINS = get("Skins", "wot-skins", "worldoftanks/wot" +
				"-skins");

		private WorldOfTanks() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.WORLD_OF_TANKS);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.WORLD_OF_TANKS, path, mainCurseForgePath);
		}
	}

	public static final class Terraria {
		@SerializedName("Maps")
		public static final ProjectType MAPS = get("Maps", "maps", "terraria/maps");

		private Terraria() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.TERRARIA);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.TERRARIA, path, mainCurseForgePath);
		}
	}

	public static final class Rift {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons", "rift/addons");

		private Rift() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.RIFT);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.RIFT, path, mainCurseForgePath);
		}
	}

	public static final class RunesOfMagic {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "addons", "rom/addons");

		private RunesOfMagic() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.RUNES_OF_MAGIC);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.RUNES_OF_MAGIC, path, mainCurseForgePath);
		}
	}

	public static final class Skyrim {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "mods", "skyrim/mods");

		private Skyrim() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.SKYRIM);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.SKYRIM, path, mainCurseForgePath);
		}
	}

	public static final class TheSecretWorld {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "tsw-mods", "tsw/tsw-mods");

		private TheSecretWorld() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.THE_SECRET_WORLD);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.THE_SECRET_WORLD, path,
					mainCurseForgePath);
		}
	}

	public static final class TheElderScrollsOnline {
		@SerializedName("Addons")
		public static final ProjectType ADDONS = get("Addons", "teso-addons", "teso/teso-addons");

		private TheElderScrollsOnline() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.THE_ELDER_SCROLLS_ONLINE);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.THE_ELDER_SCROLLS_ONLINE, path,
					mainCurseForgePath);
		}
	}

	public static final class SecretWorldLegends {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "tswl-mods", "swlegends/tswl-mods");

		private SecretWorldLegends() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.SECRET_WORLD_LEGENDS);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.SECRET_WORLD_LEGENDS, path,
					mainCurseForgePath);
		}
	}

	public static final class SurvivingMars {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "mods", "surviving-mars/mods");

		private SurvivingMars() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.SURVIVING_MARS);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.SURVIVING_MARS, path, mainCurseForgePath);
		}
	}

	public static final class DarkestDungeon {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "dd-mods", "darkestdungeon/dd-mods");

		private DarkestDungeon() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.DARKEST_DUNGEON);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.DARKEST_DUNGEON, path, mainCurseForgePath);
		}
	}

	public static final class GrandTheftAutoV {
		@SerializedName("GTA V Mods")
		public static final ProjectType MODS = get("GTA V Mods", "gta-v-mods", "gta5/gta-v-mods");
		@SerializedName("Tools")
		public static final ProjectType TOOLS = get("Tools", "gta-v-tools", "gta5/gta-v-tools");

		private GrandTheftAutoV() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.GRAND_THEFT_AUTO_V);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.GRAND_THEFT_AUTO_V, path,
					mainCurseForgePath);
		}
	}

	public static final class StardewValley {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "mods", "stardewvalley/mods");

		private StardewValley() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.STARDEW_VALLEY);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.STARDEW_VALLEY, path, mainCurseForgePath);
		}
	}

	public static final class Staxel {
		@SerializedName("Mods")
		public static final ProjectType MODS = get("Mods", "staxel-mods", "staxel/staxel-mods");

		private Staxel() {}

		public static ProjectType valueOf(String name) {
			return ProjectType.valueOf(values(), name);
		}

		public static ProjectType[] values() {
			return ProjectType.values(CurseForgeSite.STAXEL);
		}

		private static ProjectType get(String name, String path, String mainCurseForgePath) {
			return new ProjectType(name, CurseForgeSite.STAXEL, path, mainCurseForgePath);
		}
	}

	private static final List<ProjectType> values = new TRLList<>();

	public static final ProjectType UNKNOWN =
			new ProjectType("Unknown", CurseForgeSite.UNKNOWN, "unknown", "unknown");

	public static final Pattern MAIN_CURSEFORGE_PATH_PATTERN;
	public static final String MAIN_CURSEFORGE_PATH_PATTERN_STRING;

	private final String name;
	private final String singularName;
	private final String fullName;
	private final String fullSingularName;
	private final CurseForgeSite site;
	private final Game game;
	private final String sitePath;
	private final String mainCurseForgeSitePath;
	private final Pattern mainCurseForgePattern;
	private final String mainCurseForgePatternString;
	private final URL mainCurseForgeURL;
	private final String mainCurseForgeURLString;

	static {
		//Initialize all project type classes
		Bukkit.values();
		Minecraft.values();
		KerbalSpaceProgram.values();
		WildStar.values();
		WorldOfTanks.values();
		Terraria.values();
		Rift.values();
		RunesOfMagic.values();
		Skyrim.values();
		TheSecretWorld.values();
		TheElderScrollsOnline.values();
		SecretWorldLegends.values();
		DarkestDungeon.values();
		SurvivingMars.values();
		GrandTheftAutoV.values();
		StardewValley.values();
		Staxel.values();

		//Main CurseForge path pattern

		final StringBuilder pattern = new StringBuilder();

		for(ProjectType type : values()) {
			pattern.append(type.mainCurseForgePattern).append("|");
		}

		//Remove last '|'
		pattern.setLength(pattern.length() - 1);

		MAIN_CURSEFORGE_PATH_PATTERN_STRING = pattern.toString();
		MAIN_CURSEFORGE_PATH_PATTERN = Pattern.compile(MAIN_CURSEFORGE_PATH_PATTERN_STRING);
	}

	ProjectType(String name, CurseForgeSite site, String sitePath, String mainCurseForgeSitePath) {
		this.name = name;
		singularName = StringUtils.removeLastChar(name);
		game = site.game();
		fullName = game + " " + name;
		fullSingularName = game + " " + singularName;
		this.site = site;
		this.sitePath = sitePath;

		this.mainCurseForgeSitePath = mainCurseForgeSitePath + '/';
		mainCurseForgePatternString = "^/" + this.mainCurseForgeSitePath + ".+";
		mainCurseForgePattern = Pattern.compile(mainCurseForgePatternString);
		mainCurseForgeURLString = CurseForge.URL + this.mainCurseForgeSitePath;

		URL url = null;

		try {
			url = new URL(mainCurseForgeURLString);
		} catch(MalformedURLException ignored) {
			//This will never happen
		}

		mainCurseForgeURL = url;

		values.add(this);
	}

	@Override
	public String toString() {
		return name;
	}

	public CurseForgeSite site() {
		return site;
	}

	public String sitePath() {
		return sitePath;
	}

	public String mainCurseForgeSitePath() {
		return mainCurseForgeSitePath;
	}

	public Pattern mainCurseForgePattern() {
		return mainCurseForgePattern;
	}

	public String mainCurseForgePatternString() {
		return mainCurseForgePatternString;
	}

	public URL mainCurseForgeURL() {
		return mainCurseForgeURL;
	}

	public String mainCurseForgeURLString() {
		return mainCurseForgeURLString;
	}

	public String fullName() {
		return fullName;
	}

	public Game game() {
		return game;
	}

	public String fullSingularName() {
		return fullSingularName;
	}

	public String singularName() {
		return singularName;
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

	public static boolean isValidMainCurseForgePath(String path) {
		return MAIN_CURSEFORGE_PATH_PATTERN.matcher(path).matches();
	}

	public static ProjectType fromMainCurseForgeURL(URL url) {
		if(!url.getHost().equals(CurseForge.HOST)) {
			return UNKNOWN;
		}

		for(ProjectType type : values()) {
			if(type.mainCurseForgePattern.matcher(url.getPath()).matches()) {
				return type;
			}
		}

		return UNKNOWN;
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

		return UNKNOWN;
	}
}
