package com.therandomlabs.curseapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.therandomlabs.curseapi.util.JsoupUtils;
import org.junit.jupiter.api.Test;

public class CurseAPITest {
	@Test
	public void projectDetailsShouldBeValid() throws CurseException {
		final Optional<CurseProject> optional = CurseAPI.project(285612);
		assertThat(optional).isPresent();

		final CurseProject project = optional.get();
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
		assertThat(JsoupUtils.getPlainText(project.description())).isNotEmpty();
		assertThat(project.downloadCount()).isGreaterThan(0);

		assertThat(project.latestFiles()).isNotEmpty();
		final CurseFile latestFile = project.latestFiles().first();
		assertThat(latestFile.projectID()).isEqualTo(project.id());
		assertThat(latestFile.id()).isGreaterThanOrEqualTo(10);
		assertThat(latestFile.displayName()).isNotEmpty();
		assertThat(latestFile.fileName()).isNotEmpty();
		assertThat(latestFile.uploadTime()).isNotNull();
		assertThat(latestFile.fileSize()).isGreaterThan(0);
		assertThat(latestFile.downloadURL()).isNotNull();
		assertThat(JsoupUtils.getPlainText(latestFile.changelog())).isNotEmpty();

		assertThat(project.categories()).isNotEmpty();
		assertThat(project.primaryCategory()).isIn(project.categories());
		assertThat(project.slug()).isNotEmpty();
		assertThat(project.experimental()).isFalse();
		assertThat(project.creationTime()).isNotNull();
		assertThat(project.lastUpdateTime()).isNotNull();
		assertThat(project.lastModificationTime()).isNotNull();
	}

	@Test
	public void filesShouldNotBeEmpty() throws CurseException {
		final Optional<CurseFiles> optional = CurseAPI.files(285612);
		assertThat(optional).isPresent();
		assertThat(optional.get()).isNotEmpty();
	}

	@Test
	public void fileDetailsShouldBeValid() throws CurseException {
		final Optional<CurseFile> optional = CurseAPI.file(285612, 2662898);
		assertThat(optional).isPresent();

		final CurseFile file = optional.get();
		assertThat(file.projectID()).isEqualTo(285612);
		assertThat(file.id()).isGreaterThanOrEqualTo(10);
		assertThat(file.displayName()).isNotEmpty();
		assertThat(file.fileName()).isNotEmpty();
		assertThat(file.uploadTime()).isNotNull();
		assertThat(file.fileSize()).isGreaterThan(0);
		assertThat(file.downloadURL()).isNotNull();
		assertThat(JsoupUtils.getPlainText(file.changelog())).isNotEmpty();
	}
}
