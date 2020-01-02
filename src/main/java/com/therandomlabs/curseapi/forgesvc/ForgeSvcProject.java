package com.therandomlabs.curseapi.forgesvc;

import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CursePreconditions;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.project.CurseMember;
import com.therandomlabs.curseapi.project.CurseProject;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

final class ForgeSvcProject extends CurseProject {
	private int id;
	private String name;
	private List<ForgeSvcMember> authors;
	private Set<ForgeSvcAttachment> attachments;
	private HttpUrl websiteUrl;
	private int gameId;
	private String summary;
	private int downloadCount;
	private Set<ForgeSvcCategory> categories;
	private int primaryCategoryId;
	private ForgeSvcCategorySection categorySection;
	private String slug;
	private ZonedDateTime dateCreated;
	private ZonedDateTime dateReleased;
	private ZonedDateTime dateModified;
	//"isExperimental" is spelled incorrectly in the JSON.
	@SuppressWarnings("SpellCheckingInspection")
	private boolean isExperiemental;

	//Cache.
	private transient CurseGame game;
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
		for (ForgeSvcAttachment attachment : attachments) {
			if (attachment.isDefault()) {
				return attachment.url();
			}
		}

		return CurseAPI.PLACEHOLDER_PROJECT_AVATAR;
	}

	@Override
	public HttpUrl avatarThumbnailURL() {
		for (ForgeSvcAttachment attachment : attachments) {
			if (attachment.isDefault()) {
				return attachment.thumbnailURL();
			}
		}

		return CurseAPI.PLACEHOLDER_PROJECT_AVATAR_THUMBNAIL;
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
	public CurseGame game() throws CurseException {
		if (game == null) {
			final Optional<CurseGame> optionalGame = CurseAPI.game(gameId);

			if (!optionalGame.isPresent()) {
				throw new CurseException("Could not retrieve game for project: " + this);
			}

			game = optionalGame.get();
		}

		return game;
	}

	@Override
	public void clearGameCache() {
		game = null;
	}

	@Override
	public String summary() {
		return summary;
	}

	@Override
	public Element description() throws CurseException {
		if (description == null) {
			final Optional<Element> optionalDescription = CurseAPI.projectDescription(id);

			if (!optionalDescription.isPresent()) {
				throw new CurseException("Failed to retrieve description for project: " + this);
			}

			description = optionalDescription.get();
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

			//Set the project cache if the files are ForgeSvcFiles.
			for (CurseFile file : files) {
				if (file instanceof ForgeSvcFile) {
					((ForgeSvcFile) file).setProject(this);
				}
			}
		}

		return new CurseFiles<>(files);
	}

	@Override
	public void clearFilesCache() {
		files = null;
	}

	@Override
	public HttpUrl fileURL(int fileID) {
		CursePreconditions.checkFileID(fileID, "fileID");
		return HttpUrl.get(websiteUrl + "/files/" + fileID);
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
