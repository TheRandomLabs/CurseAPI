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

package com.therandomlabs.curseapi.forgesvc;

import java.util.Optional;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseAlternateFile;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.project.CurseProject;
import okhttp3.HttpUrl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.checkerframework.framework.qual.TypeUseLocation;
import org.jsoup.nodes.Element;

//NullAway does not yet support DefaultQualifier, so we have to use SuppressWarning.
@SuppressWarnings("NullAway")
@DefaultQualifier(value = Nullable.class, locations = TypeUseLocation.FIELD)
final class ForgeSvcAlternateFile extends CurseAlternateFile {
	private final int projectID;
	private final int mainFileID;
	private final int id;

	private transient CurseProject project;
	private transient HttpUrl downloadURL;
	private transient Element changelog;
	private transient CurseFile mainFile;

	ForgeSvcAlternateFile(CurseFile mainFile, @Nullable CurseProject project) {
		projectID = mainFile.projectID();
		mainFileID = mainFile.id();
		id = mainFile.alternateFileID();

		this.project = project;
		this.mainFile = mainFile;
	}

	@Override
	public int projectID() {
		return projectID;
	}

	@SuppressWarnings("Duplicates")
	@Override
	public CurseProject project() throws CurseException {
		if (project == null) {
			final Optional<CurseProject> optionalProject = CurseAPI.project(projectID);

			if (!optionalProject.isPresent()) {
				throw new CurseException("Failed to retrieve CurseProject: " + this);
			}

			project = optionalProject.get();
		}

		return project;
	}

	@Override
	public void clearProjectCache() {
		project = null;
	}

	@Override
	public int id() {
		return id;
	}

	@NonNull
	@Override
	public HttpUrl url() throws CurseException {
		return project().fileURL(id);
	}

	@Override
	public HttpUrl downloadURL() throws CurseException {
		if (downloadURL == null) {
			final Optional<HttpUrl> optionalDownloadURL = CurseAPI.fileDownloadURL(projectID, id);

			if (!optionalDownloadURL.isPresent()) {
				throw new CurseException("Failed to retrieve download URL: " + this);
			}

			downloadURL = optionalDownloadURL.get();
		}

		return downloadURL;
	}

	@Override
	public void clearDownloadURLCache() {
		downloadURL = null;
	}

	@Override
	public Element changelog() throws CurseException {
		if (changelog == null) {
			final Optional<Element> optionalChangelog = CurseAPI.fileChangelog(projectID, id);

			if (!optionalChangelog.isPresent()) {
				throw new CurseException("Failed to retrieve changelog: " + this);
			}

			changelog = optionalChangelog.get();
		}

		return changelog;
	}

	@Override
	public void clearChangelogCache() {
		changelog = null;
	}

	@Override
	public int mainFileID() {
		return mainFileID;
	}

	@Override
	public CurseFile mainFile() throws CurseException {
		if (mainFile == null) {
			final Optional<CurseFile> optionalFile = CurseAPI.file(projectID, mainFileID);

			if (!optionalFile.isPresent()) {
				throw new CurseException("Failed to retrieve main file as CurseFile: " + this);
			}

			mainFile = optionalFile.get();
		}

		return mainFile;
	}

	@Override
	public void clearMainFileCache() {
		mainFile = null;
	}
}
