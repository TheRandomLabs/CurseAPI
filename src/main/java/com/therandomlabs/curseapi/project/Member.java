package com.therandomlabs.curseapi.project;

import java.io.Serializable;
import java.net.URL;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.curseapi.cursemeta.AddOnAuthor;
import com.therandomlabs.curseapi.util.URLUtils;

public final class Member implements Serializable {
	private static final long serialVersionUID = -5874001152475689908L;

	private final MemberType type;
	private final String username;

	Member(MemberType type, String username) {
		this.type = type == null ? MemberType.UNKNOWN : type;
		this.username = username;
	}

	public MemberType type() {
		return type;
	}

	public String username() {
		return username;
	}

	public URL url() throws CurseException {
		return URLUtils.url(urlString());
	}

	public String urlString() {
		return CurseForge.URL + "members/" + username;
	}

	@Override
	public String toString() {
		return "[type=\"" + type + "\",username=\"" + username + "\"]";
	}

	static Member[] fromAuthors(AddOnAuthor[] authors) {
		final Member[] members = new Member[authors.length];
		for(int i = 0; i < authors.length; i++) {
			members[i] = new Member(null, authors[i].Name);
		}
		return members;
	}
}
