package com.therandomlabs.curseapi.util;

import java.util.List;
import org.jsoup.nodes.Element;
import com.therandomlabs.curseapi.CurseException;

@FunctionalInterface
public interface DocumentToList<E> {
	void documentToList(Element document, List<E> list) throws CurseException;
}
