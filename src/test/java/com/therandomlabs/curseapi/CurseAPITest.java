package com.therandomlabs.curseapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.therandomlabs.curseapi.file.CurseDependencyType;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFileFilter;
import com.therandomlabs.curseapi.file.CurseFileStatus;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.project.CurseMember;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.CurseSearchQuery;
import com.therandomlabs.curseapi.project.CurseSearchSort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CurseAPITest {
	@Test
	public void projectDetailsShouldBeValid() throws CurseException {
		final Optional<CurseProject> optionalProject = CurseAPI.project(285612);
		assertThat(optionalProject).isPresent();

		final CurseProject project = optionalProject.get();
		assertThat(project.name()).isNotEmpty();

		final CurseMember author = project.author();
		assertThat(author).isNotNull();
		assertThat(author.name()).isNotEmpty();
		assertThat(author.url()).isNotNull();

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
		assertThat(project.descriptionPlainText(100)).isNotEmpty();
		assertThat(project.downloadCount()).isGreaterThan(0);

		final Optional<CurseFiles<CurseFile>> optionalFiles = CurseAPI.files(project.id());
		assertThat(optionalFiles).isPresent();
		assertThat(project.files()).isEqualTo(optionalFiles.get());

		assertThat(project.categories()).isNotEmpty();
		assertThat(project.primaryCategory()).isIn(project.categories());
		assertThat(project.url()).isNotNull();
		assertThat(project.categorySection()).isNotNull();
		assertThat(project.primaryCategory().sectionID()).isEqualTo(project.categorySection().id());
		assertThat(project.primaryCategory().slug()).isNotEmpty();
		assertThat(project.primaryCategory().url()).isNotNull();
		assertThat(project.slug()).isNotEmpty();
		assertThat(project.experimental()).isFalse();
		assertThat(project.creationTime()).isNotNull();
		assertThat(project.lastUpdateTime()).isNotNull();
		assertThat(project.lastModificationTime()).isNotNull();
	}

	@Test
	public void searchResultsShouldBeValid() throws CurseException {
		final Optional<CurseCategory> optionalCategory = CurseAPI.streamCategories().
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
		final Optional<CurseFiles<CurseFile>> optionalFiles = CurseAPI.files(285612);
		assertThat(optionalFiles).isPresent();

		final CurseFiles<CurseFile> files = optionalFiles.get();
		assertThat(files).hasSizeGreaterThan(10);

		final CurseFiles<CurseFile> sortedByOldest =
				files.withComparator(CurseFiles.SORT_BY_OLDEST);
		assertThat(sortedByOldest.first().id()).isEqualTo(2522102);

		new CurseFileFilter().
				between(2735698 - 1, 2801312 + 1).
				gameVersionStrings("1.12.2").
				apply(files);

		files.parallelMap(
				CurseFile::displayName, CurseFile::changelogPlainText
		).forEach((displayName, changelog) -> {
			assertThat(displayName).isNotEmpty();
			assertThat(changelog).isNotEmpty();
		});
	}

	@Test
	public void fileDetailsShouldBeValid() throws CurseException {
		final Optional<CurseFile> optionalFile = CurseAPI.file(308836, 2735727);
		assertThat(optionalFile).isPresent();

		final CurseFile file = optionalFile.get();
		assertThat(file.projectID()).isEqualTo(308836);
		assertThat(file.id()).isEqualTo(2735727);
		assertThat(file.url()).isNotNull();
		assertThat(file.displayName()).isNotEmpty();
		assertThat(file.nameOnDisk()).isNotEmpty();
		assertThat(file.uploadTime()).isNotNull();
		assertThat(file.fileSize()).isGreaterThan(0);
		assertThat(file.releaseType()).isNotNull();
		assertThat(file.status()).isEqualTo(CurseFileStatus.NORMAL);
		assertThat(file.downloadURL()).isNotNull();
		assertThat(file.dependencies(CurseDependencyType.REQUIRED)).isNotEmpty();
		assertThat(file.dependencies().iterator().next().project()).isNotNull();
		assertThat(file.gameVersionStrings()).isNotEmpty();
		assertThat(file.changelogPlainText()).isNotEmpty();
	}

	@Test
	public void fileDownloadURLShouldNotBeNull() throws CurseException {
		assertThat(CurseAPI.fileDownloadURL(285612, 2662898)).isPresent();
	}

	@Test
	public void fileDownloadShouldBeValid(@TempDir Path tempDirectory) throws CurseException {
		CurseAPI.downloadFileToDirectory(316059, 2839369, tempDirectory);
		assertThat(tempDirectory.resolve("All the Mods 4-0.2.5.zip")).isRegularFile();
	}

	@Test
	public void gameDetailsShouldBeValid() throws CurseException {
		final Optional<CurseGame> optionalGame = CurseAPI.game(432);
		assertThat(optionalGame).isPresent();

		final CurseGame game = optionalGame.get();
		assertThat(game.name()).isNotEmpty();
		assertThat(game.slug()).isNotEmpty();
		assertThat(game.categories()).isNotEmpty();
		assertThat(game.categorySections()).isNotEmpty();
	}

	@Test
	public void gameVersionsShouldNotBePresent() throws CurseException {
		assertThat(CurseAPI.gameVersions(432)).isNotPresent();
	}

	@Test
	public void categoriesShouldBeValid() throws CurseException {
		final Optional<Set<CurseCategory>> optionalAllCategories = CurseAPI.categories();
		assertThat(optionalAllCategories).isPresent();
		assertThat(optionalAllCategories.get()).isNotEmpty();

		final Optional<CurseGame> optionalMinecraft =
				CurseAPI.streamGames().filter(game -> "Minecraft".equals(game.name())).findAny();
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
		final Optional<CurseCategory> optionalCategory = CurseAPI.category(423);
		assertThat(optionalCategory).isPresent();

		final CurseCategory category = optionalCategory.get();
		assertThat(category.name()).isNotEmpty();
		assertThat(category.slug()).isNotEmpty();
		assertThat(category.url()).isNotNull();
		assertThat(category.avatar()).isNotNull();
	}
}
