package com.therandomlabs.curseapi.util;

import java.net.URL;
import com.therandomlabs.curseapi.CurseException;

@SuppressWarnings("unused")
public interface CurseEventHandler {
	default void preRedirect(String url) throws CurseException {}

	default void postRedirect(String originalURL, String redirectedURL) throws CurseException {}

	default void preDownloadDocument(String url) throws CurseException {}

	default void postDownloadDocument(String url) throws CurseException {}

	default void retryingJSON(int retryingIn) throws CurseException {}
}
