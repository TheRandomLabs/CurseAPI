/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2020 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.therandomlabs.curseapi.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import com.google.common.collect.Iterables;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseCategorySection;
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
	public void exceptionShouldBeThrownIfAttachmentIDIsInvalid() {
		assertThatThrownBy(() -> project.attachment(CurseAPI.MIN_ATTACHMENT_ID - 1)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should not be smaller than");
	}

	@Test
	public void attachmentsShouldBeValid() throws CurseException {
		assertThat(project.attachments()).isNotEmpty();

		final CurseAttachment attachment = Iterables.getFirst(project.attachments(), null);

		assertThat(attachment).isNotEqualTo(null);
		assertThat(attachment.toString()).isNotEmpty();
		assertThat(attachment.id()).isGreaterThanOrEqualTo(CurseAPI.MIN_ATTACHMENT_ID);
		assertThat(project.attachment(attachment.id())).isEqualTo(attachment);
		assertThat(attachment.title()).isNotEmpty();
		assertThat(attachment.descriptionPlainText()).isNotNull();
		assertThat(attachment.url()).isNotNull();
		assertThat(attachment.get()).isNotNull();
		assertThat(attachment.thumbnailURL()).isNotNull();
		assertThat(attachment.thumbnail()).isNotNull();
	}

	@Test
	public void logoShouldNotBeNullOrPlaceholder() {
		assertThat(project.logo()).
				isNotNull().
				isNotEqualTo(CurseAttachment.PLACEHOLDER_LOGO);
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
	public void primaryCategoryShouldBeValid() {
		assertThat(project.primaryCategory()).isNotNull();
		assertThat(project.primaryCategory().sectionID()).
				isGreaterThanOrEqualTo(CurseAPI.MIN_CATEGORY_SECTION_ID);
		assertThat(project.primaryCategory().slug()).isNotNull();
	}

	@Test
	public void categoriesShouldContainPrimaryCategory() {
		assertThat(project.categories()).contains(project.primaryCategory());
	}

	@Test
	public void categorySectionShouldBeValid() throws CurseException {
		final CurseCategorySection categorySection = project.categorySection();
		assertThat(categorySection).isNotNull();

		//We also test CurseCategorySection here.
		final Optional<CurseCategory> optionalCategory = CurseAPI.category(423);
		assertThat(optionalCategory).isPresent();
		final CurseCategory category = optionalCategory.get();

		final Optional<CurseCategorySection> optionalCategorySection2 = category.section();
		assertThat(optionalCategorySection2).isPresent();

		assertThat(categorySection).isNotEqualTo(null);
		assertThat(categorySection).isNotEqualTo(optionalCategorySection2.get());
		assertThat(categorySection).isEqualTo(categorySection);
		assertThat(categorySection.toString()).isNotEmpty();
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
