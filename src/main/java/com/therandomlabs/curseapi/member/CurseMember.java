package com.therandomlabs.curseapi.member;

import okhttp3.HttpUrl;

public interface CurseMember {
	int id();

	String name();

	HttpUrl url();
}
