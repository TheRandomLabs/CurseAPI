package com.therandomlabs.curseapi;

import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import okhttp3.HttpUrl;

/**
 * Provides an implementation of all or a subset of CurseAPI.
 * <p>
 * Implementations of this interface may return {@code null} to signify that a fallback
 * {@link CurseAPIProvider} should be used instead.
 */
public interface CurseAPIProvider {
	/**
	 * Returns a {@link CurseProject} instance for the specified project ID.
	 *
	 * @param id a project ID.
	 * @return a {@link CurseProject} instance for the specified project ID.
	 * @throws CurseException if an error occurs.
	 */
	CurseProject project(int id) throws CurseException;

	/**
	 * Returns a {@link CurseFiles} instance for the specified project ID.
	 *
	 * @param projectID a project ID.
	 * @return a {@link CurseFiles} instance for the specified project ID.
	 * @throws CurseException if an error occurs.
	 */
	CurseFiles files(int projectID) throws CurseException;

	/**
	 * Returns a {@link CurseFile} instance for the specified project and file ID.
	 *
	 * @param projectID a project ID.
	 * @param fileID a file ID.
	 * @return a {@link CurseFile} instance for the specified project and file ID.
	 * @throws CurseException if an error occurs.
	 */
	CurseFile file(int projectID, int fileID) throws CurseException;

	/**
	 * Returns the download URL for the specified project and file ID.
	 *
	 * @param projectID a project ID.
	 * @param fileID a file ID.
	 * @return the download URl for the specified project and file ID.
	 * @throws CurseException if an error occurs.
	 */
	HttpUrl fileDownloadURL(int projectID, int fileID) throws CurseException;
}
