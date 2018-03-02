package com.therandomlabs.curseapi;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import com.therandomlabs.curseapi.curseforge.CurseForge;

public class CurseException extends Exception {
	private static final String UNAVAILABLE_MESSAGE = "Curse and/or CurseForge seems to be " +
			"unavailable. This could be due to a bug in CurseAPI, because Curse is not working " +
			"as intended, or because Java or the system cannot access the internet.";
	private static final long serialVersionUID = -7778596309352978036L;

	public static final int CURSE_MODS = 0;
	public static final int CURSEFORGE = 1;

	public CurseException(String message) {
		super(message);
	}

	public CurseException(Throwable throwable) {
		super(getMessage(throwable), throwable);
	}

	public CurseException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public static void validateProject(URL url) throws CurseException {
		if(!CurseForge.isProject(url)) {
			throw new CurseException("The following URL is not a valid CurseForge project: " + url);
		}
	}

	public static void validateMainCurseForgeProject(URL url) throws CurseException {
		if(!CurseForge.isMainCurseForgeProject(url)) {
			throw new CurseException("The following URL is not a valid Main CurseForge project: " +
					url);
		}
	}

	public static void validateFile(URL url) throws CurseException {
		if(!CurseForge.isFile(url)) {
			throw new CurseException("The following URL is not a valid CurseForge file: " + url);
		}
	}

	public static void invalidPath(String path, MalformedURLException exception)
			throws CurseException {
		throw new CurseException("The following URL is not a valid project path: " + path,
				exception);
	}

	public static void invalidProjectID(int id) throws CurseException {
		throw new CurseException("Invalid project ID: " + id);
	}

	public static void invalidProjectID(int id, Throwable throwable) throws CurseException {
		throw new CurseException("Invalid project ID: " + id, throwable);
	}

	public static boolean isUnavailable(Throwable throwable) {
		return throwable instanceof SocketTimeoutException ||
				throwable instanceof UnknownHostException;
	}

	public static void unavailable() throws CurseException {
		throw new CurseException(UNAVAILABLE_MESSAGE);
	}

	private static String getMessage(Throwable throwable) {
		if(throwable instanceof MalformedURLException) {
			return "An invalid URL has been created by CurseAPI. Usually, this is due to an " +
					"error in the user input, but it could also be a bug in CurseAPI, or a " +
					"change that Curse has made on their end.";
		}
		if(isUnavailable(throwable)) {
			return UNAVAILABLE_MESSAGE;
		}
		return "An error has occurred that should normally be prevented by CurseAPI. " +
				"This could be a bug in CurseAPI, or a change that Curse has made on their end.";
	}
}
