package com.therandomlabs.curseapi.util;

import com.therandomlabs.curseapi.CurseException;

@SuppressWarnings("unused")
public interface CurseEventHandler {
	default void preRedirect(String url) throws CurseException {}

	default void postRedirect(String originalURL, String redirectedURL) throws CurseException {}

	default void preDownloadDocument(String url) throws CurseException {}

	default void postDownloadDocument(String url) throws CurseException {}

	default void register() throws CurseException {
		CurseEventHandling.register(this);
	}

	default void unregister() throws CurseException {
		CurseEventHandling.unregister(this);
	}
}
