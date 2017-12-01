package com.therandomlabs.curseapi.widget;

public final class MemberInfo implements Cloneable {
	public String title;
	public String username;

	@Override
	public MemberInfo clone() {
		final MemberInfo info = new MemberInfo();

		info.title = title;
		info.username = username;

		return info;
	}
}
