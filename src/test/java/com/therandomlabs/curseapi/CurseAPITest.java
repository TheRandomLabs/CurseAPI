package com.therandomlabs.curseapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.therandomlabs.curseapi.project.CurseProject;
import org.junit.jupiter.api.Test;

public class CurseAPITest {
	@Test
	public void projectDetailsShouldBeValid() throws CurseException {
		final Optional<CurseProject> optional = CurseAPI.project(238222);
		assertThat(optional).isPresent();

		final CurseProject project = optional.get();
		assertThat(project.authors()).isNotEmpty();
		assertThat(project.avatarURL()).isNotEqualTo(CurseAPI.PLACEHOLDER_PROJECT_AVATAR);
		assertThat(project.avatar()).isNotNull();
		assertThat(project.avatarThumbnailURL()).isNotEqualTo(
				CurseAPI.PLACEHOLDER_PROJECT_THUMBNAIL
		);
		assertThat(project.avatarThumbnail()).isNotNull();
	}
}
