package com.therandomlabs.curseapi.util;

import java.util.ArrayList;
import java.util.List;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.utils.runnable.RunnableWithInputAndThrowable;
import com.therandomlabs.utils.wrapper.Wrapper;
import static com.therandomlabs.utils.logging.Logging.getLogger;

public final class CurseEventHandling {
	public static final CurseEventHandler DEFAULT_EVENT_HANDLER = new DefaultCurseEventHandler();
	private static final List<CurseEventHandler> eventHandlers = new ArrayList<>(5);

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

	public static void forEach(
			RunnableWithInputAndThrowable<CurseEventHandler, CurseException> consumer)
			throws CurseException {
		final Wrapper<CurseException> exception = new Wrapper<>();

		for(CurseEventHandler eventHandler : eventHandlers) {
			try {
				consumer.run(eventHandler);
			} catch(CurseException ex) {
				exception.set(ex);
			}
		}

		if(exception.hasValue()) {
			throw exception.get();
		}
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
		public void retryingJSON(int retryingIn) {
			getLogger().debug("The requested JSON is not in the database yet. " +
					"Retrying in %s seconds...", retryingIn);
		}
	}
}
