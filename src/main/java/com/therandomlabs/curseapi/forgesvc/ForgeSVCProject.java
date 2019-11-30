package com.therandomlabs.curseapi.forgesvc;

import java.util.Set;

import com.google.common.base.MoreObjects;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.member.CurseMember;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.RetrofitUtils;
import okhttp3.HttpUrl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

final class ForgeSVCProject implements CurseProject {
	private int id;
	private String name;
	private Set<ForgeSVCMember> authors;
	private Set<ForgeSVCAttachment> attachments;
	private HttpUrl websiteUrl;
	private int gameId;
	private String summary;

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("id", id).
				add("name", name).
				add("url", websiteUrl).
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

	@Override
	public HttpUrl url() {
		return websiteUrl;
	}

	@Override
	public int gameID() {
		return gameId;
	}

	@Override
	public String summary() {
		return summary;
	}

	@Override
	public Element description() throws CurseException {
		return Jsoup.parse(RetrofitUtils.getString(ForgeSVCProvider.forgeSVC.getDescription(id)));
	}
}
