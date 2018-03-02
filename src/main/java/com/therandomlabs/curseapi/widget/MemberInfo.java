package com.therandomlabs.curseapi.widget;

import java.io.Serializable;
import com.therandomlabs.curseapi.project.MemberType;

public final class MemberInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = -5874001152475689908L;

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
