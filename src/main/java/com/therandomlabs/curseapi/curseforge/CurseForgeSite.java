package com.therandomlabs.curseapi.curseforge;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.Game;
import com.therandomlabs.curseapi.project.ProjectType;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.utils.throwable.ThrowableHandling;
import static com.therandomlabs.utils.logging.Logging.getLogger;

public enum CurseForgeSite {
	BUKKIT("dev.bukkit.org", Game.MINECRAFT),
	MINECRAFT("minecraft", Game.MINECRAFT),
	FEED_THE_BEAST("www.feed-the-beast.com", Game.MINECRAFT),
	WOW_ACE("www.wowace.com", Game.WORLD_OF_WARCRAFT),
	WORLD_OF_WARCRAFT("wow", Game.WORLD_OF_WARCRAFT),
	SC_II_MAPSTER("www.sc2mapster.com", Game.STARCRAFT_II),
	KERBAL_SPACE_PROGRAM("kerbal", Game.KERBAL_SPACE_PROGRAM),
	WILDSTAR("wildstar", Game.WILDSTAR),
	TERRARIA("terraria", Game.TERRARIA),
	WORLD_OF_TANKS("worldoftanks", Game.WORLD_OF_TANKS),
	RIFT("rift", Game.RIFT),
	RUNES_OF_MAGIC("rom", Game.RUNES_OF_MAGIC),
	SKYRIM("www.skyrimforge.com", Game.SKYRIM),
	THE_SECRET_WORLD("tsw", Game.THE_SECRET_WORLD),
	THE_ELDER_SCROLLS_ONLINE("teso", Game.THE_ELDER_SCROLLS),
	SECRET_WORLD_LEGENDS("swl", Game.SECRET_WORLD_LEGENDS),
	DARKEST_DUNGEON("darkestdungeon", Game.DARKEST_DUNGEON);

	/**
	 * A {@link Pattern} that only matches valid CurseForge hosts.
	 */
	public static final Pattern HOST_PATTERN;
	/**
	 * The string representation of {@link CurseForgeSite#HOST_PATTERN}.
	 */
	public static final String HOST_PATTERN_STRING;

	static {
		final StringBuilder pattern = new StringBuilder();

		for(CurseForgeSite site : values()) {
			pattern.append(site.pattern).append("|");
		}

		//Remove last '|'
		pattern.setLength(pattern.length() - 1);

		HOST_PATTERN_STRING = pattern.toString();
		HOST_PATTERN = Pattern.compile(HOST_PATTERN_STRING);
	}

	private final String subdomain;
	private final String host;
	private final String patternString;
	private final Pattern pattern;
	private final URL url;
	private final Game game;

	CurseForgeSite(String name, Game game) {
		subdomain = name.contains(".") ? null : name;
		host = subdomain == null ? name : name + ".curseforge.com";
		patternString = "^" + host.replaceAll("\\.", "\\.") + "$";
		pattern = Pattern.compile(patternString);

		URL url = null;
		try {
			url = new URL("https://" + host + "/");
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
	 * Returns the {@link CurseForgeSite} with the host of the specified URL.
	 *
	 * @param url a URL.
	 * @return the {@link CurseForgeSite} that matches {@code url}'s host,
	 * or {@code null} if {@code url} isn't a CurseForge site.
	 */
	public static CurseForgeSite fromURL(URL url) {
		for(CurseForgeSite site : values()) {
			if(site.pattern.matcher(url.getHost()).matches()) {
				return site;
			}
		}
		return null;
	}

	public static CurseForgeSite fromString(String string) {
		for(CurseForgeSite site : values()) {
			if(site.toString().equalsIgnoreCase(string)) {
				return site;
			}
			if(site.subdomain != null && site.subdomain.equalsIgnoreCase(string)) {
				return site;
			}
			if(site.host.equalsIgnoreCase(string)) {
				return site;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return urlString();
	}

	public String urlString() {
		return url.toString();
	}

	/**
	 * Returns this site's host.
	 *
	 * @return this site's host.
	 */
	public String host() {
		return host;
	}

	/**
	 * Returns a {@link Pattern} that will only match this site's host.
	 *
	 * @return a {@link Pattern} that will only match this site's host.
	 */
	public Pattern hostPattern() {
		return pattern;
	}

	/**
	 * Returns the string representation of {@link CurseForgeSite#hostPattern()}.
	 *
	 * @return the string representation of {@link CurseForgeSite#hostPattern()}.
	 */
	public String hostPatternString() {
		return patternString;
	}

	/**
	 * Returns this site's URL.
	 *
	 * @return this site's URL.
	 */
	public URL url() {
		return url;
	}

	public URL getURL(ProjectType projectType) {
		try {
			return new URL("https://" + host + "/" + projectType.sitePath());
		} catch(MalformedURLException ex) {
			ThrowableHandling.handleUnexpected(ex);
		}
		return null;
	}

	/**
	 * Returns the game this site is for.
	 *
	 * @return the game this site is for.
	 */
	public Game game() {
		return game;
	}

	/**
	 * Returns the URL to the project on this site with the specified slug.
	 *
	 * @param slug a slug.
	 * @return the URL to the project on this site with the specified slug.
	 * @throws CurseException if something goes wrong.
	 */
	public URL getProjectURLBySlug(String slug) throws CurseException {
		final URL project = URLUtils.url("https://" + host + "/projects/" + slug);
		CurseException.validateProject(project);
		return project;
	}

	public boolean is(String host) {
		return pattern.matcher(host).matches();
	}
}
