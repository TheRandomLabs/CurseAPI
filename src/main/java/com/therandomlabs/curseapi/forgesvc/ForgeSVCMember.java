package com.therandomlabs.curseapi.forgesvc;

import com.google.common.base.MoreObjects;
import com.therandomlabs.curseapi.member.CurseMember;
import okhttp3.HttpUrl;

public final class ForgeSVCMember implements CurseMember {
	private int userId;
	private String name;
	private HttpUrl url;

	@Override
	public int hashCode() {
		return userId;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (object == null || object.getClass() != getClass()) {
			return false;
		}

		return ((ForgeSVCMember) object).userId == userId;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("id", userId).
				add("name", name).
				add("url", url).
				toString();
	}

	@Override
	public int id() {
		return userId;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public HttpUrl url() {
		return url;
	}
}
