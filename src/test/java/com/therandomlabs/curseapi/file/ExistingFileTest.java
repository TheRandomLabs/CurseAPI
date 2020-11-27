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

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ExistingFileTest {
	private static ExistingCurseFile.Existing file;
	private static ExistingCurseFile.Existing nonexistentFile;

	@Test
	void toStringShouldNotBeEmpty() {
		assertThat(file.toString()).isNotEmpty();
	}

	@Test
	void urlShouldNotBeNullIfExistent() throws CurseException {
		assertThat(file.url()).isNotNull();
	}

	@Test
	void exceptionShouldBeThrownIfProjectIsNonexistent() {
		assertThatThrownBy(() -> nonexistentFile.project()).
				isInstanceOf(CurseException.class).
				hasMessageContaining("Project does not exist");
	}

	@Test
	void projectShouldBeValidIfExistent() throws CurseException {
		assertThat(file.project()).isNotNull().isEqualTo(file.refreshProject());
	}

	@Test
	void exceptionShouldBeThrownIfNonexistent() {
		assertThatThrownBy(() -> nonexistentFile.downloadURL()).
				isInstanceOf(CurseException.class).
				hasMessageContaining("File does not exist");
		assertThatThrownBy(() -> nonexistentFile.changelogPlainText(10)).
				isInstanceOf(CurseException.class).
				hasMessageContaining("File does not exist");
	}

	@Test
	void downloadURLShouldBeValid() throws CurseException {
		assertThat(file.downloadURL()).isNotNull().isEqualTo(file.refreshDownloadURL());
	}

	@Test
	void changelogPlainTextShouldBeValid() throws CurseException {
		final String changelog = file.changelogPlainText(10);
		assertThat(changelog).isNotEmpty();

		file.refreshChangelog();
		assertThat(file.changelogPlainText(10)).isEqualTo(changelog);
	}

	@BeforeAll
	static void getFiles() {
		file = new ExistingCurseFile.Existing(CurseAPI.MIN_PROJECT_ID, CurseAPI.MIN_FILE_ID);
		nonexistentFile = new ExistingCurseFile.Existing(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
}
