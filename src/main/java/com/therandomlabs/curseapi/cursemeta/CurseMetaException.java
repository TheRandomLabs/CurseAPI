package com.therandomlabs.curseapi.cursemeta;

import java.net.URL;
import com.therandomlabs.curseapi.CurseException;

public class CurseMetaException extends CurseException {
	private static final long serialVersionUID = 6701322810093597285L;

	public static final String UNAVAILABLE_MESSAGE = "CurseMeta seems to be unavailable. " +
			"This could be due to a bug in CurseAPI, because CurseMeta is not working " +
			"as intended, or because Java or the system cannot access the internet. " +
			"Remember that CurseMeta is a community-run project, and may not always be reliable.";

	public CurseMetaException(String message) {
		super(message);
	}

	public CurseMetaException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public CurseMetaException(String description, int status, URL url) {
		super(String.format("CurseMeta request failed. Description: \"%s\", status: %d, URL: %s",
				description, status, url));
	}

	public static void unavailable() throws CurseMetaException {
		throw new CurseMetaException(UNAVAILABLE_MESSAGE);
	}
}
