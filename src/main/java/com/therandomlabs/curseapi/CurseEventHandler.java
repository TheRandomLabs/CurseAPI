package com.therandomlabs.curseapi;

import com.therandomlabs.utils.throwable.ThrowableHandling;
import static com.therandomlabs.utils.logging.Logging.getLogger;

public interface CurseEventHandler {
	default void preRedirect(String url) {
		getLogger().debug("Redirecting URL: " + url);
	}

	default void postRedirect(String originalURL, String redirectedURL) {
		getLogger().debug("%s redirected to: %s", originalURL, redirectedURL);
	}

	default void preDownloadDocument(String url) {
		getLogger().debug("Downloading document: " + url);
	}

	default void postDownloadDocument(String url) {
		getLogger().debug("Downloaded document: " + url);
	}

	default void retrying(Exception exception, int retryingIn) {
		getLogger().warning("An unexpected error occurred:");
		ThrowableHandling.handleWithoutExit(exception);
		getLogger().warning("Retrying in %d seconds...", retryingIn);
	}
}
