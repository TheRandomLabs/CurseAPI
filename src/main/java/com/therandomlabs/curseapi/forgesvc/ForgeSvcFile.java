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

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseAlternateFile;
import com.therandomlabs.curseapi.file.CurseDependency;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFileStatus;
import com.therandomlabs.curseapi.file.CurseReleaseType;
import com.therandomlabs.curseapi.project.CurseProject;
import okhttp3.HttpUrl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.checkerframework.framework.qual.TypeUseLocation;

//NullAway does not yet support DefaultQualifier, so we have to use SuppressWarning.
@SuppressWarnings({"ConstantConditions", "MismatchedQueryAndUpdateOfCollection", "NullAway"})
@DefaultQualifier(value = Nullable.class, locations = TypeUseLocation.FIELD)
final class ForgeSvcFile extends CurseFile {
	private int projectId;
	private int id;
	private String displayName;
	private String fileName;
	private ZonedDateTime fileDate;
	private long fileLength;
	private int releaseType;
	private int fileStatus;
	private HttpUrl downloadUrl;
	private int alternateFileId;
	private Set<ForgeSvcDependency> dependencies;
	private Set<String> gameVersion;

	private transient ForgeSvcAlternateFile alternateFile;
	private transient boolean dependenciesInitialized;

	//Cache.
	private transient CurseProject project;

	@Override
	public int projectID() {
		return projectId;
	}

	@NonNull
	@Override
	public CurseProject project() throws CurseException {
		if (project == null) {
			final Optional<CurseProject> optionalProject = CurseAPI.project(projectId);

			if (!optionalProject.isPresent()) {
				throw new CurseException("Failed to retrieve CurseProject: " + this);
			}

			project = optionalProject.get();
		}

		return project;
	}

	@Override
	public CurseProject refreshProject() throws CurseException {
		project = null;
		return project();
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public String displayName() {
		return displayName;
	}

	@Override
	public String nameOnDisk() {
		return fileName;
	}

	@Override
	public ZonedDateTime uploadTime() {
		return fileDate;
	}

	@Override
	public long fileSize() {
		return fileLength;
	}

	@Override
	public CurseReleaseType releaseType() {
		return CurseReleaseType.fromID(releaseType);
	}

	@Override
	public CurseFileStatus status() {
		return CurseFileStatus.fromID(fileStatus);
	}

	@Override
	public HttpUrl downloadURL() {
		return downloadUrl;
	}

	@Override
	public int alternateFileID() {
		return alternateFileId;
	}

	@Override
	public CurseAlternateFile alternateFile() {
		if (!hasAlternateFile()) {
			return null;
		}

		if (alternateFile == null) {
			alternateFile = new ForgeSvcAlternateFile(this, project);
		}

		return alternateFile;
	}

	@Override
	public Set<CurseDependency> dependencies() {
		if (!dependenciesInitialized) {
			for (ForgeSvcDependency dependency : dependencies) {
				dependency.setDependent(this);
			}

			dependenciesInitialized = true;
		}

		return new HashSet<>(dependencies);
	}

	@Override
	public Set<String> gameVersionStrings() {
		return new LinkedHashSet<>(gameVersion);
	}

	//This is called by ForgeSvcProvider so that projectId is not 0.
	void setProjectID(int id) {
		projectId = id;
	}

	//This is called by ForgeSvcProject#files().
	void setProject(CurseProject project) {
		this.project = project;
	}
}
