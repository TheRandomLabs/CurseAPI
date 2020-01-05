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
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.game.CurseGameVersion;
import com.therandomlabs.curseapi.game.CurseGameVersionGroup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("rawtypes")
@ExtendWith(MockitoExtension.class)
public class CurseFilesTest {
	@Mock(lenient = true)
	private static CurseGameVersionGroup mockVersionGroup;
	@Mock(lenient = true)
	private static CurseGameVersion mockVersion;

	private static CurseFiles<CurseFile> files;

	@Test
	public void cloneShouldReturnValidValue() {
		final CurseFiles<CurseFile> cloned = files.clone();
		assertThat(cloned).isEqualTo(files);
		files = cloned;
	}

	@Test
	public void copyConstructorShouldCopyCorrectly() {
		CurseFiles<CurseFile> copied = new CurseFiles<>(files);
		assertThat(copied).isEqualTo(files);
		files = copied;

		copied = new CurseFiles<>(files, null);
		assertThat(copied).isEqualTo(files);
		assertThat(copied).isNotEqualTo(new CurseFiles<>());
		assertThat(copied).isNotEqualTo(new CurseFiles<>(CurseFiles.SORT_BY_NEWEST));
		files = copied;
	}

	@Test
	public void filterShouldWorkCorrectly() {
		final Set<CurseGameVersion> mockVersions = new HashSet<>();

		when(mockVersionGroup.versions()).thenReturn(mockVersions);

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
		assertThat(filtered.fileWithID(2634354)).isNotNull();
		assertThat(filtered.fileWithID(2831330)).isNull();

		for (CurseFile file : filtered) {
			assertThat(file.gameVersionStrings()).contains("1.12.2");
			assertThat(file.releaseType().hasMinimumStability(CurseReleaseType.BETA)).isTrue();
		}

		final CurseFiles<CurseFile> filtered2 = files.clone();
		filtered2.filter(filter);
		assertThat(filtered2).isEqualTo(filtered);

		final CurseFiles<CurseFile> filtered3 = files.clone();

		filter.clearGameVersions();
		filter.clearNewerThan();
		filter.clearOlderThan();
		filter.clearMinimumStability();

		filter.gameVersionsArray(mockVersion);

		filter.apply(filtered3);

		for (CurseFile file : filtered3) {
			assertThat(file.gameVersionStrings()).contains("1.12.2");
		}
	}

	@Test
	public void sortingShouldWorkCorrectly() {
		final CurseFiles<CurseFile> sortedByOldest =
				files.withComparator(CurseFiles.SORT_BY_OLDEST);
		assertThat(sortedByOldest.first().id()).isEqualTo(2522102);
	}

	@Test
	public void parallelMapProducesValidList() throws CurseException {
		final CurseFiles<CurseFile> smallerFiles = files.clone();
		new CurseFileFilter().olderThan(2581245).apply(smallerFiles);
		assertThat(smallerFiles.parallelMap(CurseFile::changelogPlainText, Collectors.toList())).
				hasSameSizeAs(smallerFiles);
	}

	@Test
	public void parallelMapProducesValidMap() throws CurseException {
		final CurseFiles<CurseFile> smallerFiles = files.clone();
		new CurseFileFilter().olderThan(2581245).apply(smallerFiles);
		assertThat(smallerFiles.parallelMap(
				CurseFile::displayName,
				CurseFile::changelogPlainText
		)).hasSameSizeAs(smallerFiles);
	}

	@BeforeAll
	public static void getFiles() throws CurseException {
		final Optional<CurseFiles<CurseFile>> optionalFiles = CurseAPI.files(285612);
		assertThat(optionalFiles).isPresent();
		files = optionalFiles.get();
	}
}
