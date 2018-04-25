package com.therandomlabs.curseapi;

public class CurseUnavailableException extends CurseException {
	private static final long serialVersionUID = -5744500834687538210L;

	public CurseUnavailableException() {
		super("Curse and/or CurseForge appears to be offline.");
	}
}
