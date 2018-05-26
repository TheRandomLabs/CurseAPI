package com.therandomlabs.curseapi.util;

public class CloneException extends RuntimeException {
	private static final long serialVersionUID = -1718467136961470352L;

	public CloneException(Class<?> callerClass) {
		super("An error occurred while cloning a " + callerClass.getSimpleName() + " instance");
	}
}
