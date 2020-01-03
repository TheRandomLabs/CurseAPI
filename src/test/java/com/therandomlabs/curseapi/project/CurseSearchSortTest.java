/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 TheRandomLabs
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
