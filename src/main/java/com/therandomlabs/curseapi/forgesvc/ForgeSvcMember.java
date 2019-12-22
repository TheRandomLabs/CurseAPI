package com.therandomlabs.curseapi.forgesvc;

import com.therandomlabs.curseapi.project.CurseMember;
import okhttp3.HttpUrl;

final class ForgeSvcMember extends CurseMember {
	private int userId;
	private String name;
	private HttpUrl url;

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
