package com.therandomlabs.curseapi.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class CurseSearchSortTest {
	@Test
	public void exceptionShouldBeThrownIfIDIsInvalid() {
		assertThatThrownBy(() -> CurseSearchSort.fromID(-1)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should not be below 0");

		assertThatThrownBy(() -> CurseSearchSort.fromID(6)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should not be above 5");
	}

	@Test
	public void fromIDShouldReturnCorrectSearchSort() {
		assertThat(CurseSearchSort.fromID(2)).isEqualTo(CurseSearchSort.LAST_UPDATED);
	}
}
