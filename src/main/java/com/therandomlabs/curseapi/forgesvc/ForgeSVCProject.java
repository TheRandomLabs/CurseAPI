package com.therandomlabs.curseapi.forgesvc;

import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.project.CurseCategory;
import com.therandomlabs.curseapi.project.CurseMember;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.RetrofitUtils;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

final class ForgeSVCProject extends CurseProject {
	private int id;
	private String name;
	private List<ForgeSVCMember> authors;
	private Set<ForgeSVCAttachment> attachments;
	private HttpUrl websiteUrl;
	private int gameId;
	private String summary;
	private int downloadCount;
	private Set<ForgeSVCCategory> categories;
	private int primaryCategoryId;
	private String slug;
	private ZonedDateTime dateCreated;
	private ZonedDateTime dateReleased;
	private ZonedDateTime dateModified;
	//"isExperimental" is spelled incorrectly in the JSON.
	private boolean isExperiemental;

	@Override
	public int id() {
		return id;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public CurseMember author() {
		return authors.get(0);
	}

	@Override
	public Set<CurseMember> authors() {
		return new LinkedHashSet<>(authors);
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
		return RetrofitUtils.getElement(ForgeSVCProvider.FORGESVC.getDescription(id));
	}

	@Override
	public int downloadCount() {
		return downloadCount;
	}

	@Override
	public CurseFiles files() throws CurseException {
		return new CurseFiles(RetrofitUtils.execute(ForgeSVCProvider.FORGESVC.getFiles(id)));
	}

	@Override
	public CurseCategory primaryCategory() {
		for (CurseCategory category : categories) {
			if (category.id() == primaryCategoryId) {
				return category;
			}
		}

		//This should never happen.
		throw new IllegalStateException("Primary category not found");
	}

	@Override
	public Set<CurseCategory> categories() {
		return new LinkedHashSet<>(categories);
	}

	@Override
	public String slug() {
		return slug;
	}

	@Override
	public ZonedDateTime creationTime() {
		return dateCreated;
	}

	@Override
	public ZonedDateTime lastUpdateTime() {
		return dateReleased;
	}

	@Override
	public ZonedDateTime lastModificationTime() {
		return dateModified;
	}

	@Override
	public boolean experimental() {
		return isExperiemental;
	}
}
