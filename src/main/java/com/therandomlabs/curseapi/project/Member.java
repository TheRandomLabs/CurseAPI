package com.therandomlabs.curseapi.project;

import java.io.Serializable;
import com.therandomlabs.curseapi.widget.MemberInfo;

public final class Member implements Serializable {
	private static final long serialVersionUID = -5874001152475689908L;

	private final MemberType type;
	private final String username;

	private Member(MemberType type, String username) {
		this.type = type;
		this.username = username;
	}

	public MemberType type() {
		return type;
	}

	public String username() {
		return username;
	}

	static Member fromMemberInfo(MemberInfo info) {
		return new Member(info.title, info.username);
	}

	@Override
	public String toString() {
		return "[type=\"" + type + "\",username=\"" + username + "\"]";
	}
}
