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

package com.therandomlabs.curseapi.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Path;

import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.BasicCurseFile;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MoshiUtilsTest {
	private static final int PROJECT_ID = 100000;
	private static final int FILE_ID = 200000;

	private static final String JSON = String.format(
			"{\n  \"fileID\": %s,\n  \"projectID\": %s\n}\n", FILE_ID, PROJECT_ID
	);

	@Test
	void exceptionShouldBeThrownIfJsonIsInvalid() {
		assertThatThrownBy(() -> MoshiUtils.fromJSON("", BasicCurseFile.Immutable.class)).
				isInstanceOf(CurseException.class).
				hasMessageContaining("Failed to read JSON");
	}

	@Test
	void exceptionShouldBeThrownIfPathDoesNotExist(@TempDir Path tempDirectory) {
		final Path nonexistent = tempDirectory.resolve("nonexistent").resolve("nonexistent.json");

		assertThatThrownBy(() -> MoshiUtils.fromJSON(nonexistent, BasicCurseFile.Immutable.class)).
				isInstanceOf(CurseException.class).
				hasMessageContaining("Failed to read JSON");

		assertThatThrownBy(() -> MoshiUtils.toJSON(
				new BasicCurseFile.Immutable(PROJECT_ID, FILE_ID), nonexistent
		)).isInstanceOf(CurseException.class).hasMessageContaining("Failed to write JSON");
	}

	@Test
	void jsonShouldConvertToValidImmutableFile(@TempDir Path tempDirectory)
			throws CurseException, IOException {
		final Path json = tempDirectory.resolve("file.json");

		try (BufferedSink sink = Okio.buffer(Okio.sink(json))) {
			sink.writeUtf8(JSON);
		}

		final BasicCurseFile.Immutable file =
				MoshiUtils.fromJSON(json, BasicCurseFile.Immutable.class);

		assertThat(file).isNotNull();
		assertThat(file.projectID()).isEqualTo(PROJECT_ID);
		assertThat(file.id()).isEqualTo(FILE_ID);
	}

	@Test
	void immutableFileShouldConvertToValidJSON(@TempDir Path tempDirectory)
			throws CurseException, IOException {
		final Path json = tempDirectory.resolve("file.json");
		MoshiUtils.toJSON(new BasicCurseFile.Immutable(PROJECT_ID, FILE_ID), json);

		try (BufferedSource source = Okio.buffer(Okio.source(json))) {
			assertThat(source.readUtf8()).isEqualTo(JSON);
		}
	}
}
