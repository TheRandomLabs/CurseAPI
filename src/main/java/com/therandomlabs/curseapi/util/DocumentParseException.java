package com.therandomlabs.curseapi.util;

import com.therandomlabs.curseapi.CurseException;
import org.jsoup.nodes.Element;

public class DocumentParseException extends CurseException {
	private static final long serialVersionUID = 6853014828739664306L;

	public DocumentParseException(Element document, Throwable cause) {
		super("Failed to parse document: " + document, cause);
	}
}
