package com.therandomlabs.curseapi.cursemeta;

public class NullCurseMetaException extends CurseMetaException {
	private static final long serialVersionUID = -3767808464866893892L;

	public NullCurseMetaException(String url) {
		super("null data from CurseMeta URL: " + url);
	}
}
