package com.therandomlabs.curseapi.cursemeta;

import java.io.Serializable;
import java.net.URL;
import com.therandomlabs.curseapi.project.ProjectStage;
import com.therandomlabs.curseapi.project.ProjectStatus;
import com.therandomlabs.curseapi.util.CloneException;

public class AddOn implements Cloneable, Serializable {
	private static final long serialVersionUID = -1888244561525104543L;

	public AddOnAttachment[] Attachments;
	public AddOnAuthor[] Authors;
	public URL AvatarUrl;
	public AddOnCategory[] Categories;
	public AddOnCategorySection CategorySection;
	public int CommentCount;
	public int DefaultFileId;
	public URL DonationUrl;
	public double DownloadCount;
	public URL ExternalUrl;
	public int GameId;
	public int GamePopularityRank;
	public AddOnLatestFiles[] GameVersionLatestFiles;
	public int IconId;
	public int Id;
	public int InstallCount;
	public int IsFeatured;
	public AddOnFile[] LatestFiles;
	public int Likes;
	public String Name;
	public PackageType PackageType;
	public double PopularityScore;
	public String PrimaryAuthorName;
	public URL PrimaryCategoryAvatarUrl;
	public int PrimaryCategoryId;
	public String PrimaryCategoryName;
	public int Rating;
	public ProjectStage Stage;
	public ProjectStatus Status;
	public String Summary;
	public URL WebSiteURL;

	@Override
	public AddOn clone() {
		try {
			final AddOn addon = (AddOn) super.clone();

			addon.Attachments = CloneException.tryClone(Attachments);
			addon.Authors = CloneException.tryClone(Authors);
			addon.Categories = CloneException.tryClone(Categories);
			addon.CategorySection = CategorySection.clone();
			addon.LatestFiles = CloneException.tryClone(LatestFiles);

			return addon;
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
