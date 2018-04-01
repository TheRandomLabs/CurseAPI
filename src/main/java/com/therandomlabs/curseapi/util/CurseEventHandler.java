package com.therandomlabs.curseapi.util;

import com.therandomlabs.curseapi.CurseException;

public interface CurseEventHandler {
	default void preRedirect(String url) throws CurseException {}

	default void postRedirect(String originalURL, String redirectedURL) throws CurseException {}

	default void preDownloadDocument(String url) throws CurseException {}

	default void postDownloadDocument(String url) throws CurseException {}

	default void retrying(Exception exception, int retryingIn) throws CurseException {}
}
