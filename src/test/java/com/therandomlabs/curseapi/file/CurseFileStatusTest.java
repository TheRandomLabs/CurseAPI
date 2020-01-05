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

public class CurseFileStatusTest {
	@Test
	public void idsShouldBeCorrect() {
		assertThat(CurseFileStatus.STATUS_1.id()).isEqualTo(1);
		assertThat(CurseFileStatus.STATUS_2.id()).isEqualTo(2);
		assertThat(CurseFileStatus.STATUS_3.id()).isEqualTo(3);
		assertThat(CurseFileStatus.NORMAL.id()).isEqualTo(4);
		assertThat(CurseFileStatus.REJECTED.id()).isEqualTo(5);
		assertThat(CurseFileStatus.STATUS_6.id()).isEqualTo(6);
		assertThat(CurseFileStatus.DELETED.id()).isEqualTo(7);
		assertThat(CurseFileStatus.ARCHIVED.id()).isEqualTo(8);
	}

	@Test
	public void exceptionShouldBeThrownIfIDIsInvalid() {
		assertThatThrownBy(() -> CurseFileStatus.fromID(0)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should be positive");
		assertThatThrownBy(() -> CurseFileStatus.fromID(9)).
				isInstanceOf(IllegalArgumentException.class).
				hasMessageContaining("should not be above 8");
	}

	@Test
	public void fromIDShouldReturnCorrectValues() {
		assertThat(CurseFileStatus.fromID(1)).isEqualTo(CurseFileStatus.STATUS_1);
		assertThat(CurseFileStatus.fromID(2)).isEqualTo(CurseFileStatus.STATUS_2);
		assertThat(CurseFileStatus.fromID(3)).isEqualTo(CurseFileStatus.STATUS_3);
		assertThat(CurseFileStatus.fromID(4)).isEqualTo(CurseFileStatus.NORMAL);
		assertThat(CurseFileStatus.fromID(5)).isEqualTo(CurseFileStatus.REJECTED);
		assertThat(CurseFileStatus.fromID(6)).isEqualTo(CurseFileStatus.STATUS_6);
		assertThat(CurseFileStatus.fromID(7)).isEqualTo(CurseFileStatus.DELETED);
		assertThat(CurseFileStatus.fromID(8)).isEqualTo(CurseFileStatus.ARCHIVED);
	}
}
