package com.therandomlabs.curseapi.forgesvc;

import java.util.Set;

import com.google.common.base.MoreObjects;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.member.CurseMember;
import com.therandomlabs.curseapi.project.CurseProject;
import okhttp3.HttpUrl;

public final class ForgeSVCProject implements CurseProject {
	private int id;
	private String name;
	private Set<ForgeSVCMember> authors;
	private Set<ForgeSVCAttachment> attachments;

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("id", id).
				add("name", name).
				toString();
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Set<? extends CurseMember> authors() {
		return authors;
	}

	@Override
	public HttpUrl avatarURL() {
		for (ForgeSVCAttachment attachment : attachments) {
			if (attachment.isDefault()) {
				return attachment.url();
			}
		}

		return CurseAPI.PLACEHOLDER_PROJECT_AVATAR;
	}

	@Override
	public HttpUrl avatarThumbnailURL() {
		for (ForgeSVCAttachment attachment : attachments) {
			if (attachment.isDefault()) {
				return attachment.thumbnailURL();
			}
		}

		return CurseAPI.PLACEHOLDER_PROJECT_THUMBNAIL;
	}
}
