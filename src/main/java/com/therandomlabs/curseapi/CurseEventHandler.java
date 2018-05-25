package com.therandomlabs.curseapi;

import java.net.URL;
import com.therandomlabs.utils.throwable.ThrowableHandling;
import static com.therandomlabs.utils.logging.Logging.getLogger;

public interface CurseEventHandler {
	default void preRedirect(URL url) {
		getLogger().debug("Redirecting URL: " + url);
	}

	default void postRedirect(URL originalURL, URL redirectedURL) {
		getLogger().debug("%s redirected to: %s", originalURL, redirectedURL);
	}

	default void preDownloadDocument(URL url) {
		getLogger().debug("Downloading document: " + url);
	}

	default void postDownloadDocument(URL url) {
		getLogger().debug("Downloaded document: " + url);
	}

	default void retrying(Exception exception, int retryingIn) {
		getLogger().warning("An unexpected error occurred:");
		ThrowableHandling.handleWithoutExit(exception);
		getLogger().warning("Retrying in %d seconds...", retryingIn);
	}
}
