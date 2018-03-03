package com.therandomlabs.curseapi.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.collection.CacheMap;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.concurrent.ThreadUtils;
import com.therandomlabs.utils.misc.StopSwitch;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.network.NetworkUtils;
import com.therandomlabs.utils.runnable.RunnableWithInput;

public final class DocumentUtils {
	private static final Map<Element, Map<String, String>> values = new HashMap<>(150);
	private static final CacheMap<String, Document> documents =
			new CacheMap<>(150, true, entry -> values.remove(entry.getValue()));

	private DocumentUtils() {}

	@FunctionalInterface
	public interface DocumentToList<E> {
		void documentToList(Element document, List<E> list) throws CurseException;
	}

	//Taken and adapted from
	//https://github.com/jhy/jsoup/blob/master/src/main/java/org/jsoup/examples/HtmlToPlainText.java
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
			} else if(name.equals("a")) {
				append("[");
			} else if(name.equals("li")) {
				append("\n * ");
			} else if(name.equals("dt")) {
				append("  ");
			} else if(StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr")) {
				append("\n");

				if(name.equals("h3")) {
					//Just for you, mezz, and your ridiculously long changelogs.
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
				append(String.format("](%s)", node.absUrl("href")));
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

	public static String read(String url) throws CurseException, IOException {
		return read(URLUtils.url(url));
	}

	public static String read(URL url) throws CurseException, IOException {
		CurseEventHandling.forEach(eventHandler ->
				eventHandler.preDownloadDocument(url.toString()));

		final String string = NetworkUtils.read(url);

		CurseEventHandling.forEach(eventHandler ->
				eventHandler.postDownloadDocument(url.toString()));

		return string;
	}

	public static Document get(String url) throws CurseException {
		return get(URLUtils.url(url));
	}

	public static Document get(URL url) throws CurseException {
		if(documents.containsKey(url.toString())) {
			return documents.get(url.toString());
		}

		try {
			final String html = read(url);
			if(html == null) {
				CurseException.unavailable();
			}

			final Document document = Jsoup.parse(html);
			document.setBaseUri(url.toString());
			documents.put(url.toString(), document);
			return document;
		} catch(IOException ex) {
			throw new CurseException("An error occurred while reading from the URL: " + url, ex);
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
				final String[] split = part.split("=");
				final int index = split.length < 3 ? 0 : Integer.parseInt(split[2]);

				switch(split[0]) {
				case "attr":
					element = element.getElementsByAttribute(split[1]).get(index);
					break;
				case "class":
					element = element.getElementsByClass(split[1]).get(index);
					break;
				case "tag":
					element = element.getElementsByTag(split[1]).get(index);
					break;
				default:
					return null;
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
		Map<String, String> cached = values.get(document);
		if(cached != null) {
			final String value = cached.get(data);
			if(value != null) {
				return value;
			}
		}

		try {
			final String lastPart = ArrayUtils.last(data.split(";"));
			final Element element = get(document,
					StringUtils.removeLastChars(data, lastPart.length() + 1));
			final String[] split = lastPart.split("=");

			final String value;

			switch(split[0]) {
			case "redirectAbsUrl":
			case "absUrl":
				final String absUrl = element.absUrl(split[1]);
				value = split[0].equals("absUrl") ? absUrl : URLUtils.redirect(absUrl).toString();
				break;
			case "class":
				final int index = split.length < 2 ? 0 : Integer.parseInt(split[1]);
				value = element.classNames().toArray(new String[0])[index];
				break;
			case "attr":
				value = element.attr(split[1]);
				break;
			case "html":
				value = element.html();
				break;
			case "text":
				value = element.text();
				break;
			default:
				return null;
			}

			if(cached == null) {
				cached = new ConcurrentHashMap<>(2);
			}

			cached.put(data, value);
			values.put(document, cached);

			return value;
		} catch(NumberFormatException | IndexOutOfBoundsException | NullPointerException ex) {
			throw new CurseException(ex);
		}
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

	public static <E> TRLList<E> iteratePages(String baseURL, DocumentToList<E> documentToList,
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

	public static void clearCache() {
		documents.clear();
	}

	public static void clearCache(String url) {
		documents.remove(url);
	}

	public static void clearCache(URL url) {
		clearCache(url.toString());
	}

	public static int getCacheSize() {
		return documents.getCacheSize();
	}

	public static void setCacheSize(int size) {
		documents.setCacheSize(size);
	}
}
