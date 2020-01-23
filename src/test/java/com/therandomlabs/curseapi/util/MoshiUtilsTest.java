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

public class MoshiUtilsTest {
	private static final int PROJECT_ID = 100000;
	private static final int FILE_ID = 200000;

	private static final String JSON = String.format(
			"{\n  \"fileID\": %s,\n  \"projectID\": %s\n}\n", FILE_ID, PROJECT_ID
	);

	@Test
	public void exceptionShouldBeThrownIfJsonIsInvalid() throws CurseException {
		assertThatThrownBy(() -> MoshiUtils.fromJSON("", BasicCurseFile.Immutable.class)).
				isInstanceOf(CurseException.class).
				hasMessageContaining("Failed to read JSON");
	}

	@Test
	public void jsonShouldConvertToValidImmutableFile(@TempDir Path tempDirectory)
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
	public void immutableFileShouldConvertToValidJSON(@TempDir Path tempDirectory)
			throws CurseException, IOException {
		final Path json = tempDirectory.resolve("file.json");
		MoshiUtils.toJSON(new BasicCurseFile.Immutable(PROJECT_ID, FILE_ID), json);

		try (BufferedSource source = Okio.buffer(Okio.source(json))) {
			assertThat(source.readUtf8()).isEqualTo(JSON);
		}
	}
}
