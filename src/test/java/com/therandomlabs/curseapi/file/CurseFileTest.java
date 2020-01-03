package com.therandomlabs.curseapi.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.util.Optional;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.project.CurseProject;
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
		assertThat(file.toCurseFile()).get().isSameAs(file);
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
	public void downloadURLShouldNotBeNull() {
		assertThat(file.downloadURL()).isNotNull();
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
	public void dependenciesShouldBeValid() {
		assertThat(dependentFile.dependencies()).isNotEmpty();

		for (CurseDependency dependency : dependentFile.dependencies()) {
			assertThat(dependency).isEqualTo(dependency);
			assertThat(dependency).isNotEqualTo(null);
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
