package com.therandomlabs.curseapi.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.game.CurseGameVersion;
import com.therandomlabs.curseapi.game.CurseGameVersionGroup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CurseFilesTest {
	private static CurseFiles<CurseFile> files;

	@Test
	public void cloneShouldReturnValidValue() {
		assertThat(files.clone()).isEqualTo(files);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void filterShouldWorkCorrectly() {
		final Set<CurseGameVersion> mockVersions = new HashSet<>();

		final CurseGameVersionGroup mockVersionGroup = mock(CurseGameVersionGroup.class);
		when(mockVersionGroup.versions()).thenReturn(mockVersions);

		final CurseGameVersion mockVersion = mock(CurseGameVersion.class);
		when(mockVersion.versionString()).thenReturn("1.12.2");
		when(mockVersion.versionGroup()).thenReturn(mockVersionGroup);

		mockVersions.add(mockVersion);

		//We also test CurseFileFilter here.
		final CurseFileFilter filter = new CurseFileFilter().
				gameVersionGroupsArray(mockVersionGroup).
				between(
						new BasicCurseFile.Immutable(285612, 2522102),
						new BasicCurseFile.Immutable(285612, 2831330)
				).
				minimumStability(CurseReleaseType.BETA).
				clone();

		final CurseFiles<CurseFile> filtered = files.clone();
		filter.apply(filtered);

		assertThat(filtered.fileWithID(2522102)).isNull();
		assertThat(filtered.fileWithID(2733070)).isNotNull();
		assertThat(filtered.fileWithID(2831330)).isNull();

		for (CurseFile file : filtered) {
			assertThat(file.gameVersionStrings()).contains("1.12.2");
			assertThat(file.releaseType().matchesMinimumStability(CurseReleaseType.BETA)).isTrue();
		}

		final CurseFiles<CurseFile> filtered2 = files.clone();
		filtered2.filter(filter);
		assertThat(filtered2).isEqualTo(filtered);
	}

	@Test
	public void sortingShouldWorkCorrectly() {
		final CurseFiles<CurseFile> sortedByOldest =
				files.withComparator(CurseFiles.SORT_BY_OLDEST);
		assertThat(sortedByOldest.first().id()).isEqualTo(2522102);
	}

	@Test
	public void parallelMapProducesValidOutput() throws CurseException {
		assertThat(files.parallelMap(
				file -> file.project().name(),
				CurseFile::changelogPlainText
		)).hasSameSizeAs(files);
	}

	@BeforeAll
	public static void getFiles() throws CurseException {
		final Optional<CurseFiles<CurseFile>> optionalFiles = CurseAPI.files(285612);
		assertThat(optionalFiles).isPresent();
		files = optionalFiles.get();
	}
}
