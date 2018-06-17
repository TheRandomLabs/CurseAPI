package com.therandomlabs.curseapi;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

public class CurseException extends Exception {
	private static final long serialVersionUID = -7778596309352978036L;

	public CurseException(String message) {
		super(message);
	}

	protected CurseException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public static CurseException fromThrowable(Throwable throwable) {
		return fromThrowable(throwable, null);
	}

	public static CurseException fromThrowable(Throwable throwable, URL url) {
		return fromThrowable(null, throwable, url);
	}

	public static CurseException fromThrowable(String message, Throwable throwable) {
		return fromThrowable(message, throwable, null);
	}

	public static CurseException fromThrowable(String message, Throwable throwable, URL url) {
		if(throwable instanceof SocketTimeoutException ||
				throwable instanceof UnknownHostException) {
			return new CurseUnavailableException(url);
		}

		if(message == null) {
			message = "";
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
