package com.therandomlabs.curseapi.forgesvc;

import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
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
	private ForgeSVCCategorySection categorySection;
	private String slug;
	private ZonedDateTime dateCreated;
	private ZonedDateTime dateReleased;
	private ZonedDateTime dateModified;
	//"isExperimental" is spelled incorrectly in the JSON.
	private boolean isExperiemental;

	//Cache.
	private transient Element description;
	private transient CurseFiles<CurseFile> files;

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
		if (description == null) {
			description = RetrofitUtils.getElement(ForgeSVCProvider.FORGESVC.getDescription(id));
		}

		return description;
	}

	@Override
	public void clearDescriptionCache() {
		description = null;
	}

	@Override
	public int downloadCount() {
		return downloadCount;
	}

	@Override
	public CurseFiles<CurseFile> files() throws CurseException {
		if (files == null) {
			files = CurseAPI.files(id).orElse(null);

			if (files == null) {
				throw new CurseException("Failed to retrieve project files: " + this);
			}

			//Set the project cache if the files are ForgeSVCFiles.
			for (CurseFile file : files) {
				if (file instanceof ForgeSVCFile) {
					((ForgeSVCFile) file).setProject(this);
				}
			}
		}

		return files;
	}

	@Override
	public void clearFilesCache() {
		files = null;
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
	public CurseCategorySection categorySection() {
		return categorySection;
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
