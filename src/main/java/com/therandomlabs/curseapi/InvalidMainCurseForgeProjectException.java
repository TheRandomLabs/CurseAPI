package com.therandomlabs.curseapi;

import java.net.URL;
import com.therandomlabs.curseapi.util.Documents;
import org.jsoup.nodes.Document;

public class InvalidMainCurseForgeProjectException extends CurseException {
	private static final long serialVersionUID = 2830989828445649200L;

	public InvalidMainCurseForgeProjectException(URL url) {
		super("Invalid Main CurseForge project URL: " + url);
	}

	public static Document validate(URL url) throws CurseException {
		if(CurseForge.isValidMainCurseForgeProjectURL(url)) {
			final Document document = Documents.get(url);
			if(CurseForge.isMainCurseForgeProject(document)) {
				return document;
			}
		}

		throw new InvalidMainCurseForgeProjectException(url);
	}
}
