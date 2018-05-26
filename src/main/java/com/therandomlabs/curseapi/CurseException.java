package com.therandomlabs.curseapi;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import com.therandomlabs.curseapi.util.Documents;
import org.jsoup.nodes.Document;

public class CurseException extends Exception {
	private static final long serialVersionUID = -7778596309352978036L;

	public CurseException(String message) {
		super(message);
	}

	protected CurseException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public static Document validateProject(URL url) throws CurseException {
		if(CurseForge.isValidProjectURL(url)) {
			final Document document = Documents.get(url);
			if(CurseForge.isProject(document)) {
				return document;
			}
		}

		throw new CurseException("Invalid CurseForge project URL: " + url);
	}

	public static Document validateMainCurseForgeProject(URL url) throws CurseException {
		if(CurseForge.isValidMainCurseForgeProjectURL(url)) {
			final Document document = Documents.get(url);
			if(CurseForge.isMainCurseForgeProject(document)) {
				return document;
			}
		}

		throw new CurseException("Invalid Main CurseForge project URL: " + url);
	}

	public static CurseException fromThrowable(Throwable throwable) {
		return fromThrowable(null, throwable);
	}

	public static CurseException fromThrowable(String message, Throwable throwable) {
		if(throwable instanceof SocketTimeoutException ||
				throwable instanceof UnknownHostException) {
			return new CurseUnavailableException();
		}

		if(message == null) {
			message = "";
		} else {
			message += "Additional information: ";
		}

		if(throwable instanceof MalformedURLException) {
			message += "An invalid URL has been created. This could be due to a user error, " +
					"a bug in CurseAPI, or a change that Curse has made on their end.";
		} else if(message.isEmpty()) {
			message = "An unexpected error has occurred. This could be due to a user error, " +
					"a bug in CurseAPI, or a change that Curse has made on their end.";
		}

		return new CurseException(message, throwable);
	}
}
