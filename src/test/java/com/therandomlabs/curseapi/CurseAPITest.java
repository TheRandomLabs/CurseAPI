package com.therandomlabs.curseapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFileStatus;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.CurseSearchQuery;
import com.therandomlabs.curseapi.project.CurseSearchSort;
import org.junit.jupiter.api.Test;

public class CurseAPITest {
	@Test
	public void projectDetailsShouldBeValid() throws CurseException {
		final Optional<CurseProject> optionalProject = CurseAPI.project(285612);
		assertThat(optionalProject).isPresent();

		final CurseProject project = optionalProject.get();
		assertThat(project.authors()).isNotEmpty();
		assertThat(project.avatarURL()).isNotEqualTo(CurseAPI.PLACEHOLDER_PROJECT_AVATAR);
		assertThat(project.avatar()).isNotNull();
		assertThat(project.avatarThumbnailURL()).isNotEqualTo(
				CurseAPI.PLACEHOLDER_PROJECT_THUMBNAIL
		);
		assertThat(project.avatarThumbnail()).isNotNull();
		assertThat(project.url()).isNotNull();
		assertThat(project.gameID()).isEqualTo(432);
		assertThat(project.summary()).isNotEmpty();
		assertThat(project.descriptionPlainText()).isNotEmpty();
		assertThat(project.downloadCount()).isGreaterThan(0);

		final Optional<CurseFiles> optionalFiles = CurseAPI.files(project.id());
		assertThat(optionalFiles).isPresent();
		assertThat(project.files()).isEqualTo(optionalFiles.get());

		assertThat(project.categories()).isNotEmpty();
		assertThat(project.primaryCategory()).isIn(project.categories());
		assertThat(project.primaryCategory().avatar()).isNotNull();
		assertThat(project.slug()).isNotEmpty();
		assertThat(project.experimental()).isFalse();
		assertThat(project.creationTime()).isNotNull();
		assertThat(project.lastUpdateTime()).isNotNull();
		assertThat(project.lastModificationTime()).isNotNull();
	}

	@Test
	public void searchResultsShouldBeValid() throws CurseException {
		final Optional<Set<CurseCategory>> optionalCategories = CurseAPI.categories();
		assertThat(optionalCategories).isPresent();
		System.out.println(optionalCategories.get());
		final Optional<CurseCategory> optionalCategory = optionalCategories.get().stream().
				filter(category -> "Armor, Tools, and Weapons".equals(category.name())).findAny();
		assertThat(optionalCategory).isPresent();

		final CurseSearchQuery query = new CurseSearchQuery().
				category(optionalCategory.get()).
				pageSize(67).
				sortingMethod(CurseSearchSort.LAST_UPDATED);
		final Optional<List<CurseProject>> results = CurseAPI.searchProjects(query);
		assertThat(results).isPresent();
		assertThat(results.get()).hasSize(query.pageSize());
	}

	@Test
	public void filesShouldBeValid() throws CurseException {
		final Optional<CurseFiles> optionalFiles = CurseAPI.files(285612);
		assertThat(optionalFiles).isPresent();

		final CurseFiles files = optionalFiles.get();
		assertThat(files).isNotEmpty();

		final CurseFiles sortedByOldest = files.withComparator(CurseFiles.SORT_BY_OLDEST);
		assertThat(sortedByOldest.first().id()).isEqualTo(2522102);
	}

	@Test
	public void fileDetailsShouldBeValid() throws CurseException {
		final Optional<CurseFile> optionalFile = CurseAPI.file(285612, 2662898);
		assertThat(optionalFile).isPresent();

		final CurseFile file = optionalFile.get();
		assertThat(file.projectID()).isEqualTo(285612);
		assertThat(file.id()).isEqualTo(2662898);
		assertThat(file.displayName()).isNotEmpty();
		assertThat(file.nameOnDisk()).isNotEmpty();
		assertThat(file.uploadTime()).isNotNull();
		assertThat(file.fileSize()).isGreaterThan(0);
		assertThat(file.releaseType()).isNotNull();
		assertThat(file.status()).isEqualTo(CurseFileStatus.NORMAL);
		assertThat(file.downloadURL()).isNotNull();
		assertThat(file.changelogPlainText()).isNotEmpty();
	}

	@Test
	public void fileDownloadURLShouldNotBeNull() throws CurseException {
		assertThat(CurseAPI.fileDownloadURL(285612, 2662898)).isPresent();
	}

	@Test
	public void categoriesShouldBeValid() throws CurseException {
		final Optional<Set<CurseCategory>> optionalAllCategories = CurseAPI.categories();
		assertThat(optionalAllCategories).isPresent();
		assertThat(optionalAllCategories.get()).isNotEmpty();

		final Optional<Set<CurseGame>> optionalGames = CurseAPI.games();
		assertThat(optionalGames).isPresent();

		final Set<CurseGame> games = optionalGames.get();
		assertThat(games).isNotEmpty();

		final Optional<CurseGame> optionalMinecraft =
				games.stream().filter(game -> "Minecraft".equals(game.name())).findAny();
		assertThat(optionalMinecraft).isNotEmpty();

		final CurseGame minecraft = optionalMinecraft.get();
		assertThat(minecraft.categories()).isNotEmpty();

		final Optional<CurseCategorySection> optionalSection = minecraft.categorySections().
				stream().filter(section -> section.id() == 6).findAny();
		assertThat(optionalSection).isPresent();

		final CurseCategorySection section = optionalSection.get();
		assertThat(section.categories()).isNotEmpty();
	}

	@Test
	public void categoryShouldBeValid() throws CurseException {
		assertThat(CurseAPI.category(423)).isPresent();
	}
}
