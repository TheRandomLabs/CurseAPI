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

package com.therandomlabs.curseapi.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.Optional;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.project.CurseProject;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CurseFileTest {
	private static CurseFile file;
	private static CurseFile comparisonFile;
	private static CurseFile comparisonFile2;
	private static CurseFile dependentFile;

	@Test
	public void equalsShouldBeValid() {
		assertThat(file).isEqualTo(file);
		assertThat(file).isNotEqualTo(comparisonFile);
		assertThat(file).isNotEqualTo(null);
	}

	@Test
	public void toStringShouldNotBeEmpty() {
		assertThat(file.toString()).isNotEmpty();
	}

	@Test
	public void compareToShouldBeBasedOnID() {
		assertThat(file).isEqualByComparingTo(file);
		assertThat(file.compareTo(comparisonFile)).isNegative();
		assertThat(file.compareTo(comparisonFile2)).isNegative();
		assertThat(comparisonFile2.compareTo(file)).isPositive();
	}

	@Test
	public void projectIDShouldBeValid() {
		assertThat(file.projectID()).isGreaterThanOrEqualTo(CurseAPI.MIN_PROJECT_ID);
	}

	@Test
	public void projectShouldBeValid() throws CurseException {
		final CurseProject project = file.project();
		assertThat(project).isNotNull();
		assertThat(project.id()).isEqualTo(file.projectID());

		file.clearProjectCache();
		assertThat(file.project()).isEqualTo(project);
	}

	@Test
	public void sameProjectShouldReturnCorrectValues() {
		assertThatThrownBy(() -> file.sameProject(null)).
				isInstanceOf(NullPointerException.class).
				hasMessageContaining("should not be null");
		assertThat(file.sameProject(file)).isTrue();
		assertThat(file.sameProject(comparisonFile)).isTrue();
		assertThat(file.sameProject(comparisonFile2)).isFalse();
	}

	@Test
	public void idShouldBeValid() {
		assertThat(file.id()).isGreaterThanOrEqualTo(CurseAPI.MIN_FILE_ID);
	}

	@Test
	public void urlShouldNotBeNull() throws CurseException {
		assertThat(file.url()).isNotNull();
	}

	@Test
	public void olderThanShouldBeBasedOnID() {
		assertThat(file.olderThan(comparisonFile)).isFalse();
		assertThat(file.olderThan(comparisonFile2)).isFalse();
		assertThat(comparisonFile2.olderThan(file)).isTrue();
	}

	@Test
	public void newerThanShouldBeBasedOnID() {
		assertThat(file.newerThan(comparisonFile)).isTrue();
		assertThat(file.newerThan(comparisonFile2)).isTrue();
		assertThat(comparisonFile2.newerThan(file)).isFalse();
	}

	@Test
	public void toCurseFileShouldReturnSameFile() throws CurseException {
		assertThat(file.toCurseFile()).isSameAs(file);
	}

	@Test
	public void displayNameShouldNotBeNull() {
		assertThat(file.displayName()).isNotEmpty();
	}

	@Test
	public void nameOnDiskShouldNotBeEmpty() {
		assertThat(file.nameOnDisk()).isNotEmpty();
	}

	@Test
	public void mavenDependencyShouldNotBeEmpty() throws CurseException {
		assertThat(file.mavenDependency()).isNotEmpty();
	}

	@Test
	public void hasAlternateFileShouldReturnCorrectValue() {
		assertThat(file.hasAlternateFile()).isFalse();
		assertThat(comparisonFile2.hasAlternateFile()).isTrue();
	}

	@Test
	public void alternateFileIDShouldBeValid() {
		assertThat(comparisonFile2.alternateFileID()).isGreaterThanOrEqualTo(CurseAPI.MIN_FILE_ID);
	}

	@Test
	public void alternateFileShouldBeNull() {
		assertThat(file.alternateFile()).isNull();
	}

	@Test
	public void alternateFileShouldNotBeNull() {
		assertThat(comparisonFile2.alternateFile()).isNotNull();
	}

	@Test
	public void uploadTimeShouldNotBeNull() {
		assertThat(file.uploadTime()).isNotNull();
	}

	@Test
	public void fileSizeShouldBePositive() {
		assertThat(file.fileSize()).isPositive();
	}

	@Test
	public void releaseTypeShouldNotBeNull() {
		assertThat(file.releaseType()).isNotNull();
	}

	@Test
	public void statusShouldNotBeNull() {
		assertThat(file.status()).isNotNull();
	}

	@Test
	public void downloadURLShouldBeValid() {
		final HttpUrl downloadURL = file.downloadURL();
		assertThat(downloadURL).isNotNull();

		//This should be a no-op, so we use isSameAs instead of isEqualTo.
		file.clearDownloadURLCache();
		assertThat(file.downloadURL()).isSameAs(downloadURL);
	}

	@Test
	public void downloadShouldDownload(@TempDir Path tempDirectory) throws CurseException {
		final Path path = tempDirectory.resolve("randompatches-1.14.4-19.1.1.1.jar");
		file.download(path);
		assertThat(path).isRegularFile();
	}

	@Test
	public void downloadToDirectoryShouldDownloadWithCorrectName(@TempDir Path tempDirectory)
			throws CurseException {
		final Path path = file.downloadToDirectory(tempDirectory);
		assertThat(path).isRegularFile().hasFileName("randompatches-1.14.4-1.19.1.1.jar");
	}

	@Test
	public void dependenciesShouldBeValid() throws CurseException {
		assertThat(dependentFile.dependencies()).isNotEmpty();

		//We also test CurseDependency here.
		for (CurseDependency dependency : dependentFile.dependencies()) {
			assertThat(dependency).isEqualTo(dependency);
			assertThat(dependency).isNotEqualTo(null);

			final CurseDependency mockDependency = mock(CurseDependency.class);
			when(mockDependency.projectID()).thenReturn(dependency.projectID());
			assertThat(dependency).isEqualTo(mockDependency);

			final CurseProject project = dependency.project();
			assertThat(project).isNotNull();
			dependency.clearProjectCache();
			assertThat(dependency.project()).isEqualTo(project);

			assertThat(dependency.toString()).isNotEmpty();
			assertThat(dependency.dependent()).isEqualTo(dependentFile);
		}

		assertThat(dependentFile.dependencies(CurseDependencyType.REQUIRED)).isNotEmpty();
		assertThat(dependentFile.dependencies(CurseDependencyType.INCOMPATIBLE)).isEmpty();
	}

	@Test
	public void gameVersionStringsShouldNotBeEmpty() {
		assertThat(file.gameVersionStrings()).isNotEmpty();
	}

	@Test
	public void gameVersionsShouldBeEmpty() throws CurseException {
		assertThat(file.gameVersions()).isEmpty();
		file.clearGameVersionsCache();
		assertThat(file.gameVersions()).isEmpty();
	}

	@Test
	public void gameVersionGroupsShouldBeEmpty() throws CurseException {
		assertThat(file.gameVersionGroups()).isEmpty();
	}

	@Test
	public void exceptionShouldBeThrownIfMaxLineLengthIsInvalid() {
		assertThatThrownBy(() -> file.changelogPlainText(0)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should be greater than");
	}

	@Test
	public void changelogPlainTextShouldBeValid() throws CurseException {
		final String changelog = file.changelogPlainText(10);
		assertThat(changelog).isNotEmpty();

		file.clearChangelogCache();
		assertThat(file.changelogPlainText(10)).isEqualTo(changelog);
	}

	@BeforeAll
	public static void getFile() throws CurseException {
		final Optional<CurseFile> optionalFile = CurseAPI.file(285612, 2803612);
		assertThat(optionalFile).isPresent();
		file = optionalFile.get();

		final Optional<CurseFile> optionalComparisonFile = CurseAPI.file(285612, 2727963);
		assertThat(optionalComparisonFile).isPresent();
		comparisonFile = optionalComparisonFile.get();

		final Optional<CurseFile> optionalComparisonFile2 = CurseAPI.file(258205, 2758483);
		assertThat(optionalComparisonFile2).isPresent();
		comparisonFile2 = optionalComparisonFile2.get();

		final Optional<CurseFile> optionalDependentFile = CurseAPI.file(64578, 2322348);
		assertThat(optionalDependentFile).isPresent();
		dependentFile = optionalDependentFile.get();
	}
}
