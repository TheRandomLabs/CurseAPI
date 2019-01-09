package com.therandomlabs.curseapi.cursemeta;

import java.net.URL;
import com.therandomlabs.curseapi.file.FileStatus;
import com.therandomlabs.curseapi.project.ProjectStatus;
import com.therandomlabs.curseapi.util.Utils;

public final class CMAddon implements Cloneable {
	public int id;
	public String name;
	public CMAuthor[] authors;
	public CMAttachment[] attachments;
	public URL websiteUrl;
	public int gameId;
	public String summary;
	public int defaultFileId;
	public int commentCount;
	public int downloadCount;
	public int rating;
	public int installCount;
	public CMFile[] latestFiles;
	public CMCategory[] categories;
	public String primaryAuthorName;
	public URL externalUrl;
	public int status;
	public int stage;
	public URL donationUrl;
	public String primaryCategoryName;
	public URL primaryCategoryAvatarUrl;
	public int likes;
	public CMCategorySection categorySection;
	public int packageType;
	public URL avatarUrl;
	public String slug;
	public URL clientUrl;
	public int isFeatured;
	public double popularityScore;
	public int gamePopularityRank;
	public String fullDescription;
	public String gameName;
	public String portalName;
	public String sectionName;
	public String dateModified;
	public String dateCreated;
	public String dateReleased;
	public boolean isAvailabe;
	public String categoryList;

	@Override
	public CMAddon clone() {
		try {
			final CMAddon addon = (CMAddon) super.clone();

			addon.authors = Utils.tryClone(authors);
			addon.attachments = Utils.tryClone(attachments);
			addon.latestFiles = Utils.tryClone(latestFiles);
			addon.categories = Utils.tryClone(categories);
			addon.categorySection = categorySection.clone();

			return addon;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}

	public ProjectStatus status() {
		switch(status) {
		case 1:
			return ProjectStatus.NORMAL;
		//Beyond this point, I'm just guessing
		case 2:
			return ProjectStatus.HIDDEN;
		case 3:
			return ProjectStatus.DELETED;
		default:
			return ProjectStatus.UNKNOWN;
		}
	}
}
