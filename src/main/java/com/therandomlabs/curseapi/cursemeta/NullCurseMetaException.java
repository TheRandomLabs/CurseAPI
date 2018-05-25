package com.therandomlabs.curseapi.cursemeta;

import java.net.URL;

public class NullCurseMetaException extends CurseMetaException {
	private static final long serialVersionUID = -3767808464866893892L;

	public NullCurseMetaException(URL url) {
		super("null data from CurseMeta URL " + url + ". This is usually due to invalid input.");
	}
}
