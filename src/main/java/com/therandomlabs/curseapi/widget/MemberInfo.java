package com.therandomlabs.curseapi.widget;

import java.io.Serializable;
import com.therandomlabs.curseapi.project.MemberType;

public final class MemberInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = -5874001152475689908L;

	public MemberType title;
	public String username;

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

	@Override
	public int hashCode() {
		return title.hashCode() + username.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof MemberInfo && object.hashCode() == hashCode();
	}
}
