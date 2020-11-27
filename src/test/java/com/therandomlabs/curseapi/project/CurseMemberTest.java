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

package com.therandomlabs.curseapi.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CurseMemberTest {
	private static CurseMember member;
	private static CurseMember comparisonMember;

	@Test
	void equalsShouldBeValid() {
		assertThat(member).isEqualTo(member);
		assertThat(member).isNotEqualTo(comparisonMember);
		assertThat(member).isNotEqualTo(null);
	}

	@Test
	void toStringShouldNotBeEmpty() {
		assertThat(member.toString()).isNotEmpty();
	}

	@Test
	void compareToShouldBeBasedOnName() {
		assertThat(member).isEqualByComparingTo(member);
		assertThat(member.compareTo(comparisonMember)).isNegative();
	}

	@Test
	void idShouldBeValid() {
		assertThat(member.id()).isPositive();
	}

	@Test
	void nameShouldNotBeEmpty() {
		assertThat(member.name()).isNotEmpty();
	}

	@Test
	void urlShouldNotBeNull() {
		assertThat(member.url()).isNotNull();
	}

	@BeforeAll
	static void getMember() throws CurseException {
		final Optional<CurseProject> optionalProject = CurseAPI.project(285612);
		assertThat(optionalProject).isPresent();
		member = optionalProject.get().author();
		assertThat(member).isNotNull();

		final Optional<CurseProject> optionalProject2 = CurseAPI.project(CurseAPI.MIN_PROJECT_ID);
		assertThat(optionalProject2).isPresent();
		comparisonMember = optionalProject2.get().author();
		assertThat(comparisonMember).isNotNull();
	}
}
