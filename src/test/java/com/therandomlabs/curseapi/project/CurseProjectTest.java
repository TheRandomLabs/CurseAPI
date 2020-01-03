package com.therandomlabs.curseapi.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.game.CurseGame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CurseProjectTest {
	private static CurseProject project;
	private static CurseProject comparisonProject;

	@Test
	public void equalsShouldBeValid() {
		assertThat(project).isEqualTo(project);
		assertThat(project).isNotEqualTo(comparisonProject);
		assertThat(project).isNotEqualTo(null);
	}

	@Test
	public void toStringShouldNotBeEmpty() {
		assertThat(project.toString()).isNotEmpty();
	}

	@Test
	public void compareToShouldBeBasedOnName() {
		assertThat(project).isEqualByComparingTo(project);
		assertThat(project.compareTo(comparisonProject)).isNegative();
	}

	@Test
	public void idShouldBeValid() {
		assertThat(project.id()).isGreaterThanOrEqualTo(CurseAPI.MIN_PROJECT_ID);
	}

	@Test
	public void nameShouldNotBeEmpty() {
		assertThat(project.name()).isNotEmpty();
	}

	@Test
	public void authorShouldNotBeNull() {
		assertThat(project.author()).isNotNull();
	}

	@Test
	public void authorsShouldContainAuthor() {
		assertThat(project.authors()).contains(project.author());
	}

	@Test
	public void avatarURLShouldNotBeNullOrPlaceholder() {
		assertThat(project.avatarURL()).
				isNotNull().
				isNotEqualTo(CurseAPI.PLACEHOLDER_PROJECT_AVATAR);
	}

	@Test
	public void avatarShouldNotBeNull() throws CurseException {
		assertThat(project.avatar()).isNotNull();
	}

	@Test
	public void avatarThumbnailURLShouldNotBeNullOrPlaceholder() {
		assertThat(project.avatarThumbnailURL()).
				isNotNull().
				isNotEqualTo(CurseAPI.PLACEHOLDER_PROJECT_AVATAR_THUMBNAIL);
	}

	@Test
	public void avatarThumbnailShouldNotBeNull() throws CurseException {
		assertThat(project.avatarThumbnail()).isNotNull();
	}

	@Test
	public void urlShouldNotBeNull() {
		assertThat(project.url()).isNotNull();
	}

	@Test
	public void gameIDShouldBeValid() {
		assertThat(project.gameID()).isGreaterThanOrEqualTo(CurseAPI.MIN_GAME_ID);
	}

	@Test
	public void gameShouldBeValid() throws CurseException {
		final CurseGame game1 = project.game();
		assertThat(game1).isNotNull();

		project.clearGameCache();

		final CurseGame game2 = project.game();
		assertThat(game1.id()).isEqualTo(project.gameID());

		assertThat(game1).isEqualTo(game2);
	}

	@Test
	public void summaryShouldNotBeEmpty() {
		assertThat(project.summary()).isNotEmpty();
	}

	@Test
	public void exceptionShouldBeThrownIfMaxLineLengthIsInvalid() throws CurseException {
		assertThatThrownBy(() -> project.descriptionPlainText(0)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should be greater than 0");
	}

	@Test
	public void descriptionPlainTextShouldBeValid() throws CurseException {
		final String description = project.descriptionPlainText();
		assertThat(description).isNotEmpty();

		project.clearDescriptionCache();
		assertThat(project.descriptionPlainText()).isEqualTo(description);
	}

	@Test
	public void downloadCountShouldBePositive() throws CurseException {
		assertThat(project.downloadCount()).isPositive();
	}

	@Test
	public void filesShouldBeValid() throws CurseException {
		final CurseFiles<CurseFile> files = project.files();
		assertThat(files).isNotEmpty();

		for (CurseFile file : files) {
			assertThat(file.project()).isEqualTo(project);
		}

		project.clearFilesCache();
		assertThat(project.files()).isEqualTo(files);
	}

	@Test
	public void exceptionShouldBeThrownIfFileIDIsInvalid() {
		assertThatThrownBy(() -> project.fileURL(CurseAPI.MIN_FILE_ID - 1)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should not be smaller than");
	}

	@Test
	public void fileURLShouldBeValid() throws CurseException {
		assertThat(project.fileURL(CurseAPI.MIN_FILE_ID)).isNotNull();
	}

	@Test
	public void primaryCategoryShouldNotBeNull() {
		assertThat(project.primaryCategory()).isNotNull();
	}

	@Test
	public void categoriesShouldContainPrimaryCategory() {
		assertThat(project.categories()).contains(project.primaryCategory());
	}

	@Test
	public void categorySectionShouldNotBeNull() {
		assertThat(project.categorySection()).isNotNull();
	}

	@Test
	public void slugShouldNotBeNull() {
		assertThat(project.slug()).isNotNull();
	}

	@Test
	public void creationTimeShouldNotBeNull() {
		assertThat(project.creationTime()).isNotNull();
	}

	@Test
	public void lastUpdateTimeShouldNotBeNull() {
		assertThat(project.lastUpdateTime()).isNotNull();
	}

	@Test
	public void lastModificationTimeShouldNotBeNull() {
		assertThat(project.lastModificationTime()).isNotNull();
	}

	@Test
	public void experimentalShouldBeFalse() {
		assertThat(project.experimental()).isFalse();
	}

	@BeforeAll
	public static void getProject() throws CurseException {
		final Optional<CurseProject> optionalProject = CurseAPI.project(285612);
		assertThat(optionalProject).isPresent();
		project = optionalProject.get();

		final Optional<CurseProject> optionalComparisonProject = CurseAPI.project(258205);
		assertThat(optionalComparisonProject).isPresent();
		comparisonProject = optionalComparisonProject.get();
	}
}
