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
package com.therandomlabs.curseapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.therandomlabs.curseapi.game.CurseCategory;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.game.CurseGameVersion;
import com.therandomlabs.curseapi.game.CurseGameVersionGroup;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.CurseSearchQuery;
import com.therandomlabs.curseapi.project.CurseSearchSort;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CurseAPITest {
	@Test
	public void shouldThrowExceptionIfInvalidProjectID() {
		assertThatThrownBy(() -> CurseAPI.project(CurseAPI.MIN_PROJECT_ID - 1)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should not be smaller than");
	}

	@Test
	public void projectShouldBePresentIfExistent() throws CurseException {
		assertThat(CurseAPI.project(CurseAPI.MIN_PROJECT_ID)).isPresent();
	}

	@Test
	public void projectShouldNotBePresentIfNonexistent() throws CurseException {
		assertThat(CurseAPI.project(Integer.MAX_VALUE)).isNotPresent();
	}

	@Test
	public void exceptionShouldBeThrownIfMaxLineLengthIsInvalid() {
		assertThatThrownBy(() -> CurseAPI.projectDescriptionPlainText(CurseAPI.MIN_PROJECT_ID, 0)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should be greater than");

		assertThatThrownBy(() -> CurseAPI.fileChangelogPlainText(
				CurseAPI.MIN_PROJECT_ID, CurseAPI.MIN_FILE_ID, 0
		)).isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should be greater than");
	}

	@Test
	public void projectDescriptionPlainTextShouldNotBeEmpty() throws CurseException {
		assertThat(CurseAPI.projectDescriptionPlainText(
				CurseAPI.MIN_PROJECT_ID
		)).get().asString().isNotEmpty();
	}

	@Test
	public void searchResultsShouldBeValid() throws CurseException {
		final Optional<CurseGame> optionalGame = CurseAPI.game(432);
		assertThat(optionalGame).isPresent();

		final Optional<CurseCategory> optionalCategory = CurseAPI.streamCategories().
				filter(category -> "Armor, Tools, and Weapons".equals(category.name())).
				findAny();
		assertThat(optionalCategory).isPresent();

		//We also test CurseSearchQuery.
		final CurseSearchQuery query = new CurseSearchQuery().
				game(optionalGame.get()).
				category(optionalCategory.get()).
				gameVersionString("1.12.2").
				pageIndex(6).
				pageSize(20).
				searchFilter("Weapons").
				sortingMethod(CurseSearchSort.LAST_UPDATED);

		assertThat(query.toString()).isNotEmpty();
		assertThat(CurseAPI.searchProjects(query.clone())).get().asList().hasSizeGreaterThan(15);

		query.clearCategorySection();
		query.clearCategory();
		query.clearGameVersionString();
		query.clearPageIndex();
		query.clearPageSize();
		query.clearSearchFilter();
		query.clearSortingMethod();

		assertThat(CurseAPI.searchProjects(query)).get().asList().hasSizeGreaterThan(15);
	}

	@Test
	public void filesShouldNotBeEmpty() throws CurseException {
		assertThat(CurseAPI.files(CurseAPI.MIN_PROJECT_ID)).get().
				asInstanceOf(InstanceOfAssertFactories.ITERABLE).
				isNotEmpty();
	}

	@Test
	public void shouldThrowExceptionIfInvalidFileID() throws CurseException {
		assertThatThrownBy(() -> CurseAPI.file(CurseAPI.MIN_PROJECT_ID, CurseAPI.MIN_FILE_ID - 1)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should not be smaller than");
	}

	@Test
	public void fileShouldBePresentIfExistent() throws CurseException {
		assertThat(CurseAPI.file(CurseAPI.MIN_PROJECT_ID, CurseAPI.MIN_FILE_ID)).isPresent();
	}

	@Test
	public void fileShouldNotBePresentIfNonexistent() throws CurseException {
		assertThat(CurseAPI.file(CurseAPI.MIN_PROJECT_ID, CurseAPI.MIN_FILE_ID + 1)).isNotPresent();
	}

	@Test
	public void fileChangelogShouldNotBeEmpty() throws CurseException {
		assertThat(CurseAPI.fileChangelogPlainText(CurseAPI.MIN_PROJECT_ID, CurseAPI.MIN_FILE_ID)).
				get().asString().isNotEmpty();
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Test
	public void fileShouldDownload(@TempDir Path tempDirectory) throws CurseException {
		final Path path = tempDirectory.resolve("assistpartymember-10900.zip");
		assertThat(CurseAPI.downloadFile(CurseAPI.MIN_PROJECT_ID, CurseAPI.MIN_FILE_ID, path)).
				isTrue();
		assertThat(path).isRegularFile();
	}

	@Test
	public void nonexistentFileShouldNotDownload(@TempDir Path tempDirectory)
			throws CurseException {
		final Path path = tempDirectory.resolve("download.zip");
		assertThat(CurseAPI.downloadFile(CurseAPI.MIN_PROJECT_ID, Integer.MAX_VALUE, path)).
				isFalse();
		assertThat(path).doesNotExist();
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Test
	public void fileShouldDownloadToDirectoryWithCorrectName(@TempDir Path tempDirectory)
			throws CurseException {
		assertThat(CurseAPI.downloadFileToDirectory(
				CurseAPI.MIN_PROJECT_ID, CurseAPI.MIN_FILE_ID, tempDirectory
		)).get().asInstanceOf(InstanceOfAssertFactories.PATH).
				isRegularFile().
				hasFileName("assistpartymember-10900.zip");
	}

	@Test
	public void nonexistentFileShouldNotDownloadToDirectory(@TempDir Path tempDirectory)
			throws CurseException {
		assertThat(CurseAPI.downloadFileToDirectory(
				CurseAPI.MIN_PROJECT_ID, Integer.MAX_VALUE, tempDirectory
		)).isNotPresent();
	}

	@Test
	public void gamesShouldNotBeEmpty() throws CurseException {
		assertThat(CurseAPI.games()).get().
				asInstanceOf(InstanceOfAssertFactories.ITERABLE).
				isNotEmpty();
		assertThat(CurseAPI.streamGames()).isNotEmpty();
	}

	@Test
	public void shouldThrowExceptionIfInvalidGameID() {
		assertThatThrownBy(() -> CurseAPI.game(CurseAPI.MIN_GAME_ID - 1)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should not be smaller than");
	}

	@Test
	public void gameShouldBePresentIfExistent() throws CurseException {
		assertThat(CurseAPI.game(CurseAPI.MIN_GAME_ID)).isPresent();
	}

	@Test
	public void gameShouldNotBePresentIfNonexistent() throws CurseException {
		assertThat(CurseAPI.game(Integer.MAX_VALUE)).isNotPresent();
	}

	@Test
	public void gameVersionsShouldNotBePresent() throws CurseException {
		assertThat(CurseAPI.gameVersions(CurseAPI.MIN_GAME_ID)).isNotPresent();
		assertThat(CurseAPI.gameVersion(CurseAPI.MIN_GAME_ID, "")).isNotPresent();
	}

	@Test
	public void shouldThrowExceptionIfInvalidCategorySectionID() {
		assertThatThrownBy(() -> CurseAPI.categories(CurseAPI.MIN_CATEGORY_SECTION_ID - 1)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should not be smaller than");
	}

	@Test
	public void categoriesShouldNotBeEmpty() throws CurseException {
		assertThat(CurseAPI.categories()).get().
				asInstanceOf(InstanceOfAssertFactories.ITERABLE).
				isNotEmpty();
		assertThat(CurseAPI.categories(CurseAPI.MIN_CATEGORY_SECTION_ID)).get().
				asInstanceOf(InstanceOfAssertFactories.ITERABLE).
				isNotEmpty();
		assertThat(CurseAPI.streamCategories()).isNotEmpty();
		assertThat(CurseAPI.streamCategories(CurseAPI.MIN_CATEGORY_SECTION_ID)).isNotEmpty();
	}

	@Test
	public void shouldThrowExceptionIfInvalidCategoryID() {
		assertThatThrownBy(() -> CurseAPI.category(CurseAPI.MIN_CATEGORY_ID - 1)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should not be smaller than");
	}

	@Test
	public void categoryShouldBePresentIfExistent() throws CurseException {
		assertThat(CurseAPI.category(CurseAPI.MIN_CATEGORY_ID)).isPresent();
	}

	@Test
	public void categoryShouldNotBePresentIfNonexistent() throws CurseException {
		assertThat(CurseAPI.category(Integer.MAX_VALUE)).isNotPresent();
	}

	@SuppressWarnings({"rawtypes", "unchecked", "RedundantSuppression"})
	@Test
	public void gameVersionGroupsShouldBeValid() throws CurseException {
		final CurseGameVersionGroup mockVersionGroup = mock(CurseGameVersionGroup.class);

		final CurseGameVersion mockVersion1 = mock(CurseGameVersion.class);
		when(mockVersion1.gameID()).thenReturn(CurseAPI.MIN_GAME_ID);
		when(mockVersion1.versionGroup()).thenCallRealMethod();

		final CurseGameVersion mockVersion2 = mock(CurseGameVersion.class);
		when(mockVersion2.gameID()).thenReturn(CurseAPI.MIN_GAME_ID);
		when(mockVersion2.versionGroup()).thenReturn(mockVersionGroup);

		final CurseGameVersion mockVersion3 = mock(CurseGameVersion.class);
		when(mockVersion3.gameID()).thenReturn(CurseAPI.MIN_GAME_ID);
		when(mockVersion3.versionGroup()).thenReturn(mockVersionGroup);

		assertThat(CurseAPI.gameVersionGroups(mockVersion1, mockVersion2, mockVersion3)).
				hasSize(1).
				contains(mockVersionGroup);
	}

	@Test
	public void parallelMapProducesValidOutput() throws CurseException {
		assertThat(CurseAPI.parallelMap(
				IntStream.range(
						CurseAPI.MIN_PROJECT_ID, CurseAPI.MIN_PROJECT_ID + 4
				).boxed().collect(Collectors.toList()),
				CurseAPI::project,
				CurseAPI::projectDescription
		)).hasSize(4);
	}

	@Test
	public void parallelMapShouldThrowCorrectly() {
		assertThatThrownBy(() -> CurseAPI.parallelMap(
				Collections.singletonList("Test exception"),
				message -> {
					throw new CurseException(message);
				},
				message -> {
					throw new CurseException(message);
				}
		)).isInstanceOf(CurseException.class).hasMessage("Test exception");

		assertThatThrownBy(() -> CurseAPI.parallelMap(
				Collections.singletonList("Test exception"),
				message -> {
					throw new RuntimeException(message);
				},
				message -> {
					throw new RuntimeException(message);
				}
		)).isInstanceOf(RuntimeException.class).hasMessage("Test exception");
	}

	@Test
	public void customProviderFunctionsCorrectly() throws CurseException {
		final Optional<CurseProject> optionalProject = CurseAPI.project(CurseAPI.MIN_PROJECT_ID);
		assertThat(optionalProject).isPresent();

		final CurseAPIProvider mockProvider = mock(CurseAPIProvider.class);
		assertThat(CurseAPI.addProvider(mockProvider, true)).isTrue();
		assertThat(CurseAPI.addProvider(mockProvider, true)).isFalse();

		assertThat(CurseAPI.project(CurseAPI.MIN_PROJECT_ID)).get().
				isEqualTo(optionalProject.get());

		assertThat(CurseAPI.removeProvider(mockProvider)).isTrue();
		assertThat(CurseAPI.addProvider(mockProvider, false)).isTrue();
		assertThat(CurseAPI.removeProvider(mockProvider)).isTrue();

		final List<CurseAPIProvider> providers = CurseAPI.providers();
		assertThat(providers).isNotEmpty();
		providers.forEach(CurseAPI::removeProvider);
		assertThat(CurseAPI.project(CurseAPI.MIN_PROJECT_ID)).isNotPresent();
		providers.forEach(provider -> CurseAPI.addProvider(provider, false));
	}
}
