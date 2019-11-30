package com.therandomlabs.curseapi.project;

import java.awt.image.BufferedImage;
import java.util.Set;

import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.member.CurseMember;
import com.therandomlabs.curseapi.util.OkHttpUtils;
import okhttp3.HttpUrl;

public interface CurseProject {
	int id();

	String name();

	Set<? extends CurseMember> authors();

	HttpUrl avatarURL();

	HttpUrl avatarThumbnailURL();

	default BufferedImage avatar() throws CurseException {
		return OkHttpUtils.readImage(avatarURL());
	}

	default BufferedImage avatarThumbnail() throws CurseException {
		return OkHttpUtils.readImage(avatarThumbnailURL());
	}
}
