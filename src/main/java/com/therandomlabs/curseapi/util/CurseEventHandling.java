package com.therandomlabs.curseapi.util;

import java.util.List;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.runnable.RunnableWithInput;
import com.therandomlabs.utils.throwable.ThrowableHandling;
import static com.therandomlabs.utils.logging.Logging.getLogger;

public final class CurseEventHandling {
	public static final CurseEventHandler DEFAULT_EVENT_HANDLER = new DefaultCurseEventHandler();

	private static final List<CurseEventHandler> eventHandlers = new TRLList<>(5);

	static {
		register(DEFAULT_EVENT_HANDLER);
	}

	private CurseEventHandling() {}

	public static void register(CurseEventHandler eventHandler) {
		eventHandlers.add(eventHandler);
	}

	public static void unregister(CurseEventHandler eventHandler) {
		eventHandlers.remove(eventHandler);
	}

	public static void forEach(RunnableWithInput<CurseEventHandler> runnable) {
		eventHandlers.forEach(runnable::run);
	}

	public static class DefaultCurseEventHandler implements CurseEventHandler {
		@Override
		public void preRedirect(String url) {
			getLogger().debug("Redirecting URL: " + url);
		}

		@Override
		public void postRedirect(String originalURL, String redirectedURL) {
			getLogger().debug("%s redirected to: %s", originalURL, redirectedURL);
		}

		@Override
		public void preDownloadDocument(String url) {
			getLogger().debug("Downloading document: " + url);
		}

		@Override
		public void postDownloadDocument(String url) {
			getLogger().debug("Downloaded document: " + url);
		}

		@Override
		public void retrying(Exception exception, int retryingIn) {
			getLogger().warning("An unexpected error occurred:");
			ThrowableHandling.handleWithoutExit(exception);
			getLogger().warning("Retrying in %d seconds...", retryingIn);
		}
	}
}
