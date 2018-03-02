package com.therandomlabs.curseapi.curseforge;

import static com.therandomlabs.utils.logging.Logging.getLogger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.Game;
import com.therandomlabs.curseapi.util.URLUtils;

/**
 * An {@code enum} containing all of the Main CurseForge sites.
 * @author TheRandomLabs
 */
public enum MainCurseForgeSite {
	WOW_ADDONS("addons/wow", Game.WORLD_OF_WARCRAFT),
	WOW_ADDON_PACKS("addon-packs/wow", Game.WORLD_OF_WARCRAFT),
	BUKKIT_PLUGINS("bukkit-plugins/minecraft", Game.MINECRAFT),
	MC_MODS("mc-mods/minecraft", Game.MINECRAFT),
	MC_TEXTURE_PACKS("texture-packs/minecraft", Game.MINECRAFT),
	MC_WORLDS("worlds/minecraft", Game.MINECRAFT),
	MC_MODPACKS("modpacks/minecraft", Game.MINECRAFT),
	MC_CUSTOMIZATION("customization/minecraft", Game.MINECRAFT),
	MC_ADDONS("mc-addons/minecraft", Game.MINECRAFT),
	WILDSTAR_ADDONS("ws-addons/wildstar", Game.WILDSTAR),
	RUNES_OF_MAGIC_ADDONS("addons/rom", Game.RUNES_OF_MAGIC),
	THE_ELDER_SCROLLS_ADDONS("teso-addons/teso", Game.THE_ELDER_SCROLLS),
	RIFT_ADDONS("addons/rift", Game.RIFT),
	SKYRIM_MODS("mods/skyrim", Game.SKYRIM),
	THE_SECRET_WORLD_MODS("tsw-mods/tsw", Game.THE_SECRET_WORLD),
	WORLD_OF_TANKS_MODS("wot-mods/worldoftanks", Game.WORLD_OF_TANKS),
	WORLD_OF_TANKS_SKINS("wot-skins/worldoftanks", Game.WORLD_OF_TANKS),
	KERBAL_SPACE_PROGRAM_SHAREABLES("shareables/kerbal", Game.KERBAL_SPACE_PROGRAM),
	KERBAL_SPACE_PROGRAM_MODS("ksp-mods/kerbal", Game.KERBAL_SPACE_PROGRAM),
	TERRARIA_MAPS("maps/terraria", Game.TERRARIA),
	STARCRAFT_II_MAPS("maps/sc2", Game.STARCRAFT_II),
	STARCRAFT_II_ASSETS("assets/sc2", Game.STARCRAFT_II),
	SECRET_WORLD_LEGENDS("swlegends/twsl-mods", Game.SECRET_WORLD_LEGENDS),
	DARKEST_DUNGEON("darkestdungeon/dd-mods", Game.DARKEST_DUNGEON);

	/**
	 * A {@link Pattern} that only matches valid Main CurseForge paths.
	 */
	public static final Pattern PATH_PATTERN;
	/**
	 * The string representation of {@link MainCurseForgeSite#PATH_PATTERN}.
	 */
	public static final String PATH_PATTERN_STRING;

	private final String path;
	private final String patternString;
	private final Pattern pattern;
	private final URL url;
	private final Game game;

	static {
		final StringBuilder pattern = new StringBuilder();

		for(MainCurseForgeSite site : values()) {
			pattern.append(site.pattern).append("|");
		}

		//Remove last '|'
		pattern.setLength(pattern.length() - 1);

		PATH_PATTERN_STRING = pattern.toString();
		PATH_PATTERN = Pattern.compile(PATH_PATTERN_STRING);
	}

	MainCurseForgeSite(String path, Game game) {
		path += "/";
		this.path = path;
		patternString = "^/" + path + ".+";
		this.pattern = Pattern.compile(patternString);

		URL url = null;
		try {
			url = new URL(CurseForge.URL + path);
		} catch(MalformedURLException ex) {
			getLogger().fatalError("An error occurred while initializing CurseForgeSite. " +
					"This should not have occurred.");
			getLogger().printStackTrace(ex);
			System.exit(1);
		}
		this.url = url;

		this.game = game;
	}

	/**
	 * Returns this site's path.
	 * @return this site's path.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns a {@link Pattern} that will only match this site's path.
	 * @return a {@link Pattern} that will only match this site's path.
	 */
	public Pattern getPathPattern() {
		return pattern;
	}

	/**
	 * Returns the string representation of {@link MainCurseForgeSite#getPathPattern}.
	 * @return the string representation of {@link MainCurseForgeSite#getPathPattern}.
	 */
	public String getPathPatternString() {
		return patternString;
	}

	/**
	 * Returns this site's URL.
	 * @return this site's URL.
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Returns the game this site is for.
	 * @return the game this site is for.
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Returns the URL to the project on this site with the specified path.
	 * @param path a path.
	 * @return the URL to the project on this site with the specified path.
	 * @throws InvalidCurseURLException if {@code path} is not a valid path.
	 * @throws CurseAPIUnavailableException if CurseAPI is unavailable.
	 * @throws CurseException if something goes wrong. Usually this is caused by a change
	 * in the HTML code.
	 */
	public URL getProjectURLByPath(String path) throws CurseException {
		final URL project = URLUtils.url(url + path);
		CurseException.validateMainCurseForgeProject(project);
		return project;
	}

	public boolean is(String path) {
		return pattern.matcher(path).matches();
	}

	@Override
	public String toString() {
		return path;
	}

	public static boolean isValidPath(String path) {
		return PATH_PATTERN.matcher(path).matches();
	}

	/**
	 * Returns the {@link CurseModsSite} with the path of the specified URL.
	 * @param url a URL.
	 * @return the {@link CurseModsSite} that matches {@code url}'s path,
	 * or {@code null} if {@code url} isn't a Curse Mods site.
	 */
	public static MainCurseForgeSite valueOf(URL url) {
		if(!url.getHost().equals(CurseForge.HOST)) {
			return null;
		}

		for(MainCurseForgeSite site : values()) {
			if(site.pattern.matcher(url.getPath()).matches()) {
				return site;
			}
		}
		return null;
	}
}
