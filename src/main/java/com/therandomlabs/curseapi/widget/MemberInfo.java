package com.therandomlabs.curseapi.widget;

import com.therandomlabs.curseapi.MemberType;

public final class MemberInfo implements Cloneable {
	public MemberType title;
	public String username;

	@Override
	public MemberInfo clone() {
		final MemberInfo info = new MemberInfo();

		info.title = title;
		info.username = username;

		return info;
	}
}
