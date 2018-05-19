package com.therandomlabs.curseapi.project;

import java.io.Serializable;
import java.net.URL;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.curseapi.curseforge.CurseForgeSite;
import com.therandomlabs.curseapi.cursemeta.AddOnAuthor;
import com.therandomlabs.curseapi.util.URLUtils;

public final class Member implements Serializable {
	private static final long serialVersionUID = -5874001152475689908L;

	private final MemberType type;
	private final String username;
	private final String urlString;
	private final URL url;

	Member(MemberType type, String username) throws CurseException {
		this.type = type == null ? MemberType.UNKNOWN : type;
		this.username = username;
		urlString = getURLString(username);
		url = URLUtils.url(urlString);
	}

	public MemberType type() {
		return type;
	}

	public String username() {
		return username;
	}

	public URL url() {
		return url;
	}

	public String urlString() {
		return urlString;
	}

	@Override
	public String toString() {
		return "[type=\"" + type + "\",username=\"" + username + "\"]";
	}

	static Member[] fromAuthors(AddOnAuthor[] authors) throws CurseException {
		final Member[] members = new Member[authors.length];
		for(int i = 0; i < authors.length; i++) {
			members[i] = new Member(null, authors[i].Name);
		}
		return members;
	}

	public static String getURLString(String username) {
		return CurseForge.URL + "members/" + username;
	}

	public static String getURLString(CurseForgeSite site, String username) {
		return site.urlString() + "members/" + username;
	}
}
