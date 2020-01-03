package com.therandomlabs.curseapi;

/**
 * Represents an error that has occurred while using CurseAPI.
 */
public class CurseException extends Exception {
	private static final long serialVersionUID = 6173129011499048103L;

	/**
	 * Constructs a {@link CurseException} with the specified detail message.
	 *
	 * @param message a detail message.
	 */
	public CurseException(String message) {
		super(message);
	}

	/**
	 * Constructs a {@link CurseException} with the specified detail message and cause.
	 *
	 * @param message a detail message.
	 * @param cause a cause.
	 */
	public CurseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a {@link CurseException} with the specified cause.
	 *
	 * @param cause a cause.
	 */
	public CurseException(Throwable cause) {
		super(cause);
	}
}
