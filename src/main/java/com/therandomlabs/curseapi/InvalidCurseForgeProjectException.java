package com.therandomlabs.curseapi;

import java.net.URL;
import com.therandomlabs.curseapi.util.Documents;
import org.jsoup.nodes.Document;

public class InvalidCurseForgeProjectException extends CurseException {
	private static final long serialVersionUID = -7529090700277371026L;

	public InvalidCurseForgeProjectException(URL url) {
		super("Invalid CurseForge project URL: " + url);
	}

	public static Document validate(URL url) throws CurseException {
		if(CurseForge.isValidProjectURL(url)) {
			final Document document = Documents.get(url);
			if(CurseForge.isProject(document)) {
				return document;
			}
		}

		throw new InvalidCurseForgeProjectException(url);
	}
}
