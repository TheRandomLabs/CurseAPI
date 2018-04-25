package com.therandomlabs.curseapi.project;

import com.therandomlabs.curseapi.CurseException;

public class InvalidProjectIDException extends CurseException {
	private static final long serialVersionUID = -6454156481960479703L;

	public InvalidProjectIDException(int id) {
		super("Invalid project ID: " + id);
	}

	public InvalidProjectIDException(int id, Throwable throwable) {
		super("Invalid project ID: " + id, throwable);
	}
}
