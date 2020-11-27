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

class CurseDependencyTypeTest {
	@Test
	void idsShouldBeCorrect() {
		assertThat(CurseDependencyType.EMBEDDED_LIBRARY.id()).isEqualTo(1);
		assertThat(CurseDependencyType.OPTIONAL.id()).isEqualTo(2);
		assertThat(CurseDependencyType.REQUIRED.id()).isEqualTo(3);
		assertThat(CurseDependencyType.TOOL.id()).isEqualTo(4);
		assertThat(CurseDependencyType.INCOMPATIBLE.id()).isEqualTo(5);
		assertThat(CurseDependencyType.INCLUDE.id()).isEqualTo(6);
	}

	@Test
	void exceptionShouldBeThrownIfIDIsInvalid() {
		assertThatThrownBy(() -> CurseDependencyType.fromID(0)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should be positive");
		assertThatThrownBy(() -> CurseDependencyType.fromID(7)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should not be above 6");
	}

	@Test
	void fromIDShouldReturnCorrectValues() {
		assertThat(CurseDependencyType.fromID(1)).isEqualTo(CurseDependencyType.EMBEDDED_LIBRARY);
		assertThat(CurseDependencyType.fromID(2)).isEqualTo(CurseDependencyType.OPTIONAL);
		assertThat(CurseDependencyType.fromID(3)).isEqualTo(CurseDependencyType.REQUIRED);
		assertThat(CurseDependencyType.fromID(4)).isEqualTo(CurseDependencyType.TOOL);
		assertThat(CurseDependencyType.fromID(5)).isEqualTo(CurseDependencyType.INCOMPATIBLE);
		assertThat(CurseDependencyType.fromID(6)).isEqualTo(CurseDependencyType.INCLUDE);
	}
}
