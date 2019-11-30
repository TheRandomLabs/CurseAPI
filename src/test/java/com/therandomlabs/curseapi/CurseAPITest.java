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
	}
}
