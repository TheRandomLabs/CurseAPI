package com.therandomlabs.curseapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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

		final CurseFiles latestFiles = project.latestFiles();
		assertThat(latestFiles).isNotEmpty();

		final CurseFile latestFile = latestFiles.first();
		final Optional<CurseFile> optionalLatestFile = latestFiles.fileWithID(latestFile.id());
		assertThat(optionalLatestFile).isPresent();
		assertThat(optionalLatestFile.get()).isEqualTo(latestFile);

		assertThat(latestFile.projectID()).isEqualTo(project.id());
		assertThat(latestFile.id()).isGreaterThanOrEqualTo(10);
		assertThat(latestFile.displayName()).isNotEmpty();
		assertThat(latestFile.fileName()).isNotEmpty();
		assertThat(latestFile.uploadTime()).isNotNull();
		assertThat(latestFile.fileSize()).isGreaterThan(0);
		assertThat(latestFile.downloadURL()).isNotNull();
		assertThat(latestFile.changelogPlainText()).isNotEmpty();

		final Optional<CurseFiles> optionalFiles = CurseAPI.files(project.id());
		assertThat(optionalFiles).isPresent();
		assertThat(project.files()).isEqualTo(optionalFiles.get());

		assertThat(project.categories()).isNotEmpty();
		assertThat(project.primaryCategory()).isIn(project.categories());
		assertThat(project.slug()).isNotEmpty();
		assertThat(project.experimental()).isFalse();
		assertThat(project.creationTime()).isNotNull();
		assertThat(project.lastUpdateTime()).isNotNull();
		assertThat(project.lastModificationTime()).isNotNull();
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
		assertThat(file.id()).isGreaterThanOrEqualTo(10);
		assertThat(file.displayName()).isNotEmpty();
		assertThat(file.fileName()).isNotEmpty();
		assertThat(file.uploadTime()).isNotNull();
		assertThat(file.fileSize()).isGreaterThan(0);
		assertThat(file.downloadURL()).isNotNull();
		assertThat(file.changelogPlainText()).isNotEmpty();
	}

	@Test
	public void fileDownloadURLShouldNotBeNull() throws CurseException {
		assertThat(CurseAPI.fileDownloadURL(285612, 2662898)).isPresent();
	}
}
