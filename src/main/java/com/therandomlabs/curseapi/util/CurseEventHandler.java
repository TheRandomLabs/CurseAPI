package com.therandomlabs.curseapi.util;

import com.therandomlabs.curseapi.CurseException;

@SuppressWarnings("unused")
public interface CurseEventHandler {
	default void preRedirect(String url) throws CurseException {}

	default void postRedirect(String originalURL, String redirectedURL) throws CurseException {}

	default void preDownloadDocument(String url) throws CurseException {}

	default void postDownloadDocument(String url) throws CurseException {}

	default void retryingJSON(int retryingIn) throws CurseException {}

	default void deleting(String fileName) throws CurseException {}

	default void downloadingFile(String fileName) throws CurseException {}

	default void extracting(String fileName) throws CurseException {}

	default void downloadingMod(String modName, String fileName, int count, int total)
			throws CurseException {}

	default void installingForge(String forgeVersion) throws CurseException {}
}
