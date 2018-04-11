package com.therandomlabs.curseapi.util;

public interface CurseEventHandler {
	default void preRedirect(String url) {}

	default void postRedirect(String originalURL, String redirectedURL) {}

	default void preDownloadDocument(String url) {}

	default void postDownloadDocument(String url) {}

	default void retrying(Exception exception, int retryingIn) {}
}
