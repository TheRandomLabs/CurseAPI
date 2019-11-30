package com.therandomlabs.curseapi;

public class CurseException extends Exception {
	private static final long serialVersionUID = 6173129011499048103L;

	public CurseException(String message) {
		super(message);
	}

	public CurseException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public CurseException(Throwable throwable) {
		super(throwable);
	}
}
