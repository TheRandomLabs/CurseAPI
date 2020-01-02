package com.therandomlabs.curseapi.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CurseMemberTest {
	private static CurseMember member;
	private static CurseMember comparisonMember;

	@Test
	public void equalsShouldBeValid() {
		assertThat(member).isEqualTo(member);
		assertThat(member).isNotEqualTo(comparisonMember);
		assertThat(member).isNotEqualTo(null);
	}

	@Test
	public void toStringShouldNotBeEmpty() {
		assertThat(member.toString()).isNotEmpty();
	}

	@Test
	public void compareToShouldBeBasedOnName() {
		assertThat(member).isEqualByComparingTo(member);
		assertThat(member.compareTo(comparisonMember)).isNegative();
	}

	@Test
	public void idShouldBeValid() {
		assertThat(member.id()).isPositive();
	}

	@Test
	public void nameShouldNotBeEmpty() {
		assertThat(member.name()).isNotEmpty();
	}

	@Test
	public void urlShouldNotBeNull() {
		assertThat(member.url()).isNotNull();
	}

	@BeforeAll
	public static void getMember() throws CurseException {
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
