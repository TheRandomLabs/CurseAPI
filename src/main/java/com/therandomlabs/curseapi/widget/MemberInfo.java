package com.therandomlabs.curseapi.widget;

import com.therandomlabs.curseapi.project.MemberType;

public final class MemberInfo implements Cloneable {
	public MemberType title;
	public String username;

	@Override
	public int hashCode() {
		return title.hashCode() + username.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) {
			return true;
		}

		return object instanceof MemberInfo && object.hashCode() == hashCode();
	}

	@Override
	public MemberInfo clone() {
		try {
			return (MemberInfo) super.clone();
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	@Override
	public String toString() {
		return "[title=\"" + title + "\",username=\"" + username + "\"]";
	}
}
