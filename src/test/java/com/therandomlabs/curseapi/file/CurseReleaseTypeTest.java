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

import org.junit.jupiter.api.Test;

class CurseReleaseTypeTest {
	@Test
	void idsShouldBeCorrect() {
		assertThat(CurseReleaseType.RELEASE.id()).isEqualTo(1);
		assertThat(CurseReleaseType.BETA.id()).isEqualTo(2);
		assertThat(CurseReleaseType.ALPHA.id()).isEqualTo(3);
	}

	@Test
	void minimumStabilityShouldBeMatchedCorrectly() {
		assertThat(CurseReleaseType.ALPHA.hasMinimumStability(CurseReleaseType.RELEASE)).isFalse();
		assertThat(CurseReleaseType.ALPHA.hasMinimumStability(CurseReleaseType.BETA)).isFalse();
		assertThat(CurseReleaseType.ALPHA.hasMinimumStability(CurseReleaseType.ALPHA)).isTrue();

		assertThat(CurseReleaseType.BETA.hasMinimumStability(CurseReleaseType.RELEASE)).isFalse();
		assertThat(CurseReleaseType.BETA.hasMinimumStability(CurseReleaseType.BETA)).isTrue();
		assertThat(CurseReleaseType.BETA.hasMinimumStability(CurseReleaseType.ALPHA)).isTrue();

		assertThat(CurseReleaseType.RELEASE.hasMinimumStability(CurseReleaseType.RELEASE)).isTrue();
		assertThat(CurseReleaseType.RELEASE.hasMinimumStability(CurseReleaseType.BETA)).isTrue();
		assertThat(CurseReleaseType.RELEASE.hasMinimumStability(CurseReleaseType.ALPHA)).isTrue();
	}

	@Test
	void exceptionShouldBeThrownIfIDIsInvalid() {
		assertThatThrownBy(() -> CurseReleaseType.fromID(0)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should be positive");
		assertThatThrownBy(() -> CurseReleaseType.fromID(4)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should not be above 3");
	}

	@Test
	void fromIDShouldReturnCorrectValues() {
		assertThat(CurseReleaseType.fromID(1)).isEqualTo(CurseReleaseType.RELEASE);
		assertThat(CurseReleaseType.fromID(2)).isEqualTo(CurseReleaseType.BETA);
		assertThat(CurseReleaseType.fromID(3)).isEqualTo(CurseReleaseType.ALPHA);
	}
}
