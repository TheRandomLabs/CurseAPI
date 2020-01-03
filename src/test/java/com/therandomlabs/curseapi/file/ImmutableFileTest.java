package com.therandomlabs.curseapi.file;

import static org.assertj.core.api.Assertions.assertThat;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import org.junit.jupiter.api.Test;

public class ImmutableFileTest {
	@Test
	public void projectShouldBeNullIfNonexistent() throws CurseException {
		assertThat(new BasicCurseFile.Immutable(Integer.MAX_VALUE, Integer.MAX_VALUE).project()).
				isNull();
	}

	@Test
	public void projectShouldNotBeNullIfExistent() throws CurseException {
		assertThat(new BasicCurseFile.Immutable(
				CurseAPI.MIN_PROJECT_ID, CurseAPI.MIN_FILE_ID
		).project()).isNotNull();
	}

	@Test
	public void toCurseFileShouldNotBePresentIfNonexistent() throws CurseException {
		assertThat(new BasicCurseFile.Immutable(
				Integer.MAX_VALUE, Integer.MAX_VALUE
		).toCurseFile()).isNotPresent();
	}

	@Test
	public void toCurseFileShouldBePresentIfExistent() throws CurseException {
		assertThat(new BasicCurseFile.Immutable(
				CurseAPI.MIN_PROJECT_ID, CurseAPI.MIN_FILE_ID
		).toCurseFile()).isPresent();
	}
}
