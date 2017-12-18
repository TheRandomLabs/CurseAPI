package com.therandomlabs.curseapi.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseProject;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.concurrent.ThreadUtils;
import com.therandomlabs.utils.misc.StopSwitch;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.network.NetworkUtils;
import com.therandomlabs.utils.runnable.RunnableWithInput;

public final class DocumentUtils {
	private static final Map<String, Document> documents = new HashMap<>(50);

	private DocumentUtils() {}

	//Taken and adapted from
	//https://github.com/jhy/jsoup/blob/master/src/main/java/org/jsoup/examples/
	//HtmlToPlainText.java
	private static class FormattingVisitor implements NodeVisitor {
		private final int maxWidth;
		private int width = 0;
		private int h3s;

		private final StringBuilder text = new StringBuilder();

		FormattingVisitor(int maxWidth) {
			this.maxWidth = maxWidth < 1 ? Integer.MAX_VALUE : maxWidth;
		}

		//Hit when the node is first seen
		@Override
		public void head(Node node, int depth) {
			if(h3s > 1) {
				return;
			}

			final String name = node.nodeName();

			if(node instanceof TextNode) {
				//TextNodes carry all user-readable text in the DOM.
				append(((TextNode) node).text());
			} else if(name.equals("li")) {
				append("\n * ");
			} else if(name.equals("dt")) {
				append("  ");
			} else if(StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr")) {
				append("\n");

				if(name.equals("h3")) {
					//Just for you, JEI, and your ridiculously long changelogs.
					h3s++;
				}
			}
		}

		//Hit when all of the node's children (if any) have been visited
		@Override
		public void tail(Node node, int depth) {
			if(h3s > 1) {
				return;
			}

			final String name = node.nodeName();

			if(StringUtil.in(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5")) {
				append("\n");
			} else if(name.equals("a")) {
				append(String.format(" <%s>", absUrl(node.attr("href"))));
			}
		}

		//Appends text to the StringBuilder with a simple word wrap method
		private void append(String string) {
			if(string.startsWith("\n")) {
				//Reset counter if the string starts with a newline.
				width = 0;
			}

			if(string.equals(" ") && (text.length() == 0 ||
					StringUtil.in(text.substring(text.length() - 1), " ", "\n"))) {
				//Don't accumulate long runs of empty spaces
				return;
			}

			if(string.length() + width > maxWidth) {
				//Needs to be wrapped
				final String[] words = string.split("\\s+");
				for(int i = 0; i < words.length; i++) {
					final String word = words[i];

					//If this isn't the last word, insert a space
					if(i < words.length - 1) {
						string += ' ';
					}

					//Wrap and reset counter
					if(string.length() + width > maxWidth) {
						text.append("\n").append(word);
						width = word.length();
					} else {
						text.append(word);
						width += word.length();
					}
				}
			} else {
				text.append(string);
				width += string.length();
			}
		}

		@Override
		public String toString() {
			//I'm looking at you, Speiger.
			final String string = text.toString().replaceAll("\n\n\n", "\n");
			return string.startsWith("\n") ? string.substring(1) : string;
		}
	}

	public static String getPlainText(Element element) {
		return getPlainText(element, -1);
	}

	public static String getPlainText(Element element, int maxLineWidth) {
		final FormattingVisitor formatter = new FormattingVisitor(maxLineWidth);
		NodeTraversor.traverse(formatter, element);
		return formatter.toString();
	}

	//Jsoup's absUrl doesn't seem to work properly, so we do it ourselves
	public static String absUrl(String url) {
		//Too short to be a URL
		if(url.length() < 5) {
			return url;
		}

		try {
			new URL(url);
		} catch(MalformedURLException ex) {
			if(ex.getMessage().contains("no protocol")) {
				url = "https:" + url;
			} else {
				return url;
			}
		}

		return url;
	}

	public static Document get(String url) throws CurseException {
		return get(URLUtils.url(url));
	}

	public static Document get(URL url) throws CurseException {
		final String urlString = url.toString();

		if(documents.containsKey(urlString)) {
			return documents.get(urlString);
		}

		try {
			CurseEventHandling.forEach(eventHandler ->
					eventHandler.preDownloadDocument(urlString));

			final String html = NetworkUtils.read(url);

			CurseEventHandling.forEach(eventHandler ->
					eventHandler.postDownloadDocument(urlString));

			if(html == null) {
				CurseException.unavailable();
			}

			final Document document = Jsoup.parse(html);
			documents.put(urlString, document);
			return document;
		} catch(IOException ex) {
			throw new CurseException(ex);
		}
	}

	public static Element get(String url, String data) throws CurseException {
		return get(get(url), data);
	}

	public static Element get(URL url, String data) throws CurseException {
		return get(get(url), data);
	}

	public static Element get(Element document, String data) throws CurseException {
		try {
			final String[] parts = data.split(";");
			Element element = document;
			for(String part : parts) {
				final String[] values = part.split("=");
				final int index = values.length < 3 ? 0 : Integer.parseInt(values[2]);

				switch(values[0]) {
				case "attr":
					element = element.getElementsByAttribute(values[1]).get(index);
					break;
				case "class":
					element = element.getElementsByClass(values[1]).get(index);
					break;
				case "tag":
					element = element.getElementsByTag(values[1]).get(index);
					break;
				}
			}
			return element;
		} catch(NumberFormatException | IndexOutOfBoundsException | NullPointerException ex) {
			throw new CurseException(ex);
		}
	}

	public static String getValue(String url, String data) throws CurseException {
		return getValue(get(url), data);
	}

	public static String getValue(URL url, String data) throws CurseException {
		return getValue(get(url), data);
	}

	public static String getValue(Element document, String data) throws CurseException {
		try {
			final String lastPart = ArrayUtils.last(data.split(";"));
			final Element element = get(document,
					StringUtils.removeLastChars(data, lastPart.length() + 1));
			final String[] values = lastPart.split("=");

			switch(values[0]) {
			case "redirectAbsUrl":
			case "absUrl":
				//Jsoup seems to have trouble with absUrl, so we do it manually
				final String absUrl = absUrl(element.attr(values[1]));
				return values[0].equals("absUrl") ? absUrl : URLUtils.redirect(absUrl).toString();
			case "class":
				final int index = values.length < 2 ? 0 : Integer.parseInt(values[1]);
				return element.classNames().toArray(new String[0])[index];
			case "attr":
				return element.attr(values[1]);
			case "html":
				return element.html();
			case "text":
				return element.text();
			}
		} catch(NumberFormatException | IndexOutOfBoundsException | NullPointerException ex) {
			throw new CurseException(ex);
		}

		return null;
	}

	public static boolean isAvailable(String url) throws CurseException {
		return isAvailable(URLUtils.url(url));
	}

	public static boolean isAvailable(URL url) throws CurseException {
		try {
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			connection.disconnect();
		} catch(IOException ex) {
			if(!CurseException.isUnavailable(ex)) {
				throw new CurseException(ex);
			}
		}

		return true;
	}

	public static int getNumberOfPages(Document document) throws CurseException {
		try {
			//Don't question it. Parsing HTML can get messy, okay?

			final Elements paginations = document.getElementsByClass("b-pagination");

			if(paginations.isEmpty()) {
				return 1;
			}

			final Elements paginationItems =
					paginations.get(1).getElementsByClass("b-pagination-item");

			return Integer.parseInt(CollectionUtils.fromLast(paginationItems, 1).text());
		} catch(IndexOutOfBoundsException | NullPointerException | NumberFormatException ex) {
			throw new CurseException(ex);
		}
	}

	public static <E> List<E> iteratePages(String baseURL, DocumentToList<E> documentToList,
			RunnableWithInput<? super E> onElementAdd, StopSwitch stopSwitch)
			throws CurseException {
		baseURL += "page=";

		try {
			if(!CurseForge.is(new URL(baseURL))) {
				throw new CurseException("Invalid base URL: " + baseURL);
			}
		} catch(MalformedURLException ex) {
			throw new CurseException("Invalid base URL: " + baseURL);
		}

		//Get number of pages from the first page
		final int pages = getNumberOfPages(get(baseURL + 1));

		final String url = baseURL;

		final Map<Integer, List<E>> allData = new HashMap<>();

		try {
			ThreadUtils.splitWorkload(CurseAPI.getMaximumThreads(), pages, page -> {
				final TRLList<E> data = new TRLList<>(CurseProject.RELATIONS_PER_PAGE);
				data.setOnAdd(onElementAdd);
				allData.put(page, data);

				if(stopSwitch != null && stopSwitch.isStopped()) {
					return;
				}

				documentToList.documentToList(get(url + (page + 1)), data);
			});
		} catch(IndexOutOfBoundsException | NullPointerException | NumberFormatException ex) {
			throw new CurseException(ex);
		} catch(CurseException ex) {
			throw ex;
		}

		final TRLList<E> sortedList =
				new TRLList<>(allData.size() * CurseProject.RELATIONS_PER_PAGE);
		for(int i = 0; i < allData.size(); i++) {
			sortedList.addAll(allData.get(i));
		}
		return sortedList.toImmutableList();
	}

	public static void clearDocumentCache() {
		documents.clear();
	}
}
