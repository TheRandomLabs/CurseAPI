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

import com.therandomlabs.curseapi.CurseException;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Iterables;

class CurseFilesComparisonTest {
	@SuppressWarnings("cast")
	@Test
	void comparisonShouldBeCorrect() throws CurseException {
		final BasicCurseFile quarkUnchanged = new BasicCurseFile.Immutable(243121, 2759240);

		final BasicCurseFile enderCoreOld =
				new BasicCurseFile.Immutable(231868, 2578528).toCurseFile();
		final BasicCurseFile enderCoreNew = new BasicCurseFile.Immutable(231868, 2822401);

		final BasicCurseFile nonexistentFileNew =
				new BasicCurseFile.Immutable(Integer.MAX_VALUE, Integer.MAX_VALUE);
		final BasicCurseFile nonexistentFileOld =
				new BasicCurseFile.Immutable(Integer.MAX_VALUE, Integer.MAX_VALUE - 1);

		final BasicCurseFile jei = new BasicCurseFile.Immutable(238222, 2803400);
		final BasicCurseFile eu2 = new BasicCurseFile.Immutable(225561, 2678374);

		final CurseFiles<BasicCurseFile> files1 = new CurseFiles<>();
		files1.add(quarkUnchanged);
		files1.add(enderCoreOld);
		files1.add(nonexistentFileNew);
		files1.add(jei);

		final CurseFiles<BasicCurseFile> files2 = new CurseFiles<>();
		files2.add(quarkUnchanged);
		files2.add(enderCoreNew);
		files2.add(nonexistentFileOld);
		files2.add(eu2);

		final CurseFilesComparison<BasicCurseFile> comparison =
				CurseFilesComparison.of(files1, files2);

		assertThat(comparison.unchanged()).containsOnly(quarkUnchanged);

		assertThat(comparison.updated()).hasSize(1);
		final CurseFileChange<BasicCurseFile> update = Iterables.firstOf(comparison.updated());
		assertThat(update.isDowngrade()).isFalse();
		assertThat(update.oldFile()).isEqualTo(enderCoreOld);
		assertThat(update.olderFile()).isEqualTo(enderCoreOld);
		assertThat(update.newFile()).isEqualTo(enderCoreNew);
		assertThat(update.newerFile()).isEqualTo(enderCoreNew);

		assertThat(comparison.downgraded()).hasSize(1);
		final CurseFileChange<BasicCurseFile> downgrade =
				Iterables.firstOf(comparison.downgraded());
		assertThat(downgrade.isDowngrade()).isTrue();
		assertThat(downgrade.oldFile()).isEqualTo(nonexistentFileNew);
		assertThat(downgrade.olderFile()).isEqualTo(nonexistentFileOld);
		assertThat(downgrade.newFile()).isEqualTo(nonexistentFileOld);
		assertThat(downgrade.newerFile()).isEqualTo(nonexistentFileNew);

		assertThat(comparison.removed()).containsOnly(jei);
		assertThat(comparison.added()).containsOnly(eu2);

		//We also test CurseFileChange.
		assertThat(update).isEqualTo(update);
		assertThat(update).isNotEqualTo(downgrade);
		assertThat(update).isNotEqualTo(null);

		assertThat(update.toString()).isNotEmpty();

		assertThat(update.filesBetweenInclusive().size() - 1).
				isEqualTo(update.filesBetween().size());

		assertThat(update.projectID()).isEqualTo(enderCoreOld.projectID());
		assertThat(update.project()).isEqualTo(enderCoreOld.project());

		//Since enderCoreOld is already a CurseFile, the same reference should be returned.
		assertThat(update.oldCurseFile()).isSameAs(enderCoreOld);

		assertThat(update.oldCurseFile().id()).isEqualTo(enderCoreOld.id());
		assertThat(update.olderCurseFile()).isEqualTo(update.oldCurseFile());

		assertThat(update.newCurseFile().id()).isEqualTo(enderCoreNew.id());
		assertThat(update.newerCurseFile()).isEqualTo(update.newCurseFile());

		//This cast is necessary; for some reason, type ambiguity occurs otherwise.
		assertThat((String) update.get(file -> file.project().name())).
				isEqualTo(update.project().name());

		//CHECKSTYLE IGNORE Indentation FOR NEXT 5 LINES
		assertThatThrownBy(() -> downgrade.get(file -> file.project().name())).
				isInstanceOf(CurseException.class).
				hasMessageContaining(
						"Neither file for CurseFileChange could be retrieved as CurseFile"
				);
	}
}
