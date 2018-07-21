package com.therandomlabs.curseapi.util;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseEventHandling;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.CurseForge;
import com.therandomlabs.curseapi.CurseUnavailableException;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.io.NetUtils;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.misc.ThreadUtils;
import com.therandomlabs.utils.wrapper.IntWrapper;
import com.therandomlabs.utils.wrapper.Wrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public final class Documents {
	@FunctionalInterface
	public interface DocumentToList<E> {
		void documentToList(Element document, List<E> list) throws CurseException;
	}

	//Taken and adapted from
	//https://github.com/jhy/jsoup/blob/master/src/main/java/org/jsoup/examples/HtmlToPlainText
	// .java
	private static class FormattingVisitor implements NodeVisitor {
		private final int maxWidth;
		private final StringBuilder text = new StringBuilder();
		private int width = 0;
		private boolean wholeText;

		FormattingVisitor(int maxWidth) {
			this.maxWidth = maxWidth < 1 ? Integer.MAX_VALUE : maxWidth;
		}

		//Hit when the node is first seen
		@Override
		public void head(Node node, int depth) {
			final String name = node.nodeName();

			if(name.equals("pre")) {
				wholeText = true;
			} else if(node instanceof TextNode) {
				final TextNode text = (TextNode) node;
				if(wholeText) {
					append(text.getWholeText().trim());
				} else {
					append(text.text());
				}
			} else if(name.equals("a")) {
				append("[");
			} else if(name.equals("li")) {
				append("\n * ");
			} else if(name.equals("dt")) {
				append("  ");
			} else if(ArrayUtils.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr")) {
				append("\n");
			}
		}

		//Hit when all of the node's children (if any) have been visited
		@Override
		public void tail(Node node, int depth) {
			final String name = node.nodeName();

			if(ArrayUtils.in(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5")) {
				append("\n");
			} else if(name.equals("a")) {
				append(String.format("](%s)", node.absUrl("href")));
			} else if(name.equals("pre")) {
				wholeText = false;
			}
		}

		//Appends text to the StringBuilder with a simple word wrap method
		private void append(String string) {
			if(string.startsWith("\n")) {
				//Reset counter if the string starts with a newline.
				width = 0;
			}

			if(string.equals(" ") && (text.length() == 0 ||
					ArrayUtils.in(text.substring(text.length() - 1), " ", "\n"))) {
				//Don't accumulate long runs of empty spaces
				return;
			}

			if(string.length() + width > maxWidth) {
				//Needs to be wrapped
				final String[] words = string.split("\\s+");
				for(int i = 0; i < words.length; i++) {
					final String word = words[i];

					//Wrap and reset counter
					if(string.length() + width > maxWidth) {
						text.append("\n").append(word);
						width = word.length();
					} else {
						text.append(word);

						//If this isn't the last word, insert a space
						if(i < words.length - 1) {
							text.append(' ');
							width++;
						}

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
			return text.toString();
		}
	}

	private static final Map<Object, Map<URL, WeakReference<Document>>> cache =
			new ConcurrentHashMap<>();

	static {
		NetUtils.setUserAgent("Mozilla (https://github.com/TheRandomLabs/CurseAPI)");
	}

	private Documents() {}

	public static String getPlainText(Element element) {
		return getPlainText(element, -1);
	}

	public static String getPlainText(Element element, int maxLineWidth) {
		final FormattingVisitor formatter = new FormattingVisitor(maxLineWidth);
		NodeTraversor.traverse(formatter, element);

		//I'm looking at you, Speiger.
		final String string = formatter.toString().replaceAll("\n\n\n", "\n");
		return string.startsWith("\n") ? string.substring(1) : string;
	}

	public static String read(String url) throws CurseException, IOException {
		return read(URLs.of(url));
	}

	public static String read(URL url) throws CurseException, IOException {
		CurseEventHandling.forEach(eventHandler -> eventHandler.preDownloadDocument(url));
		final String string = NetUtils.read(url);
		CurseEventHandling.forEach(eventHandler -> eventHandler.postDownloadDocument(url));
		return string;
	}

	public static Document get(String url) throws CurseException {
		return getWithCache(url, null);
	}

	public static Document getWithCache(String url, Object cacheKey) throws CurseException {
		return getWithCache(URLs.of(url), cacheKey);
	}

	public static Document get(URL url) throws CurseException {
		return getWithCache(url, null);
	}

	public static Document getWithCache(URL url, Object cacheKey) throws CurseException {
		Map<URL, WeakReference<Document>> cacheMap = null;

		if(cacheKey != null) {
			cacheMap = cache.get(cacheKey);

			if(cacheMap != null) {
				final WeakReference<Document> reference = cacheMap.get(url);

				if(reference != null) {
					final Document document = reference.get();

					if(document != null) {
						return document;
					}
				}
			}
		}

		final Wrapper<String> htmlWrapper = new Wrapper<>();

		CurseAPI.doWithRetries(() -> {
			try {
				htmlWrapper.set(read(url));
			} catch(IOException ex) {
				throw CurseException.fromThrowable("An error occurred while reading: " + url, ex,
						url);
			}
		});

		final String html = htmlWrapper.get();

		if(html == null) {
			throw new CurseUnavailableException(url);
		}

		final Document document = Jsoup.parse(html);
		document.setBaseUri(url.toString());

		if(cacheMap != null) {
			cacheMap.put(url, new WeakReference<>(document));
		}

		return document;
	}

	public static Element get(URL url, String data) throws CurseException {
		return get(get(url), data);
	}

	public static Element get(String url, String data) throws CurseException {
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
					case "name":
						final Elements elements = element.getElementsByAttribute("name");
						for(int i = 0, j = 0; i < elements.size(); i++) {
							if(split[1].equals(elements.get(i).attr("name")) && j++ == index) {
								element = elements.get(i);
								break;
							}
						}
						break;
					default:
						return null;
				}
			}
			return element;
		} catch(NumberFormatException | IndexOutOfBoundsException | NullPointerException ex) {
			throw CurseException.fromThrowable(ex);
		}
	}

	public static String getValue(String url, String data) throws CurseException {
		return getValue(get(url), data);
	}

	public static String getValue(Element document, String data) throws CurseException {
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
					value = split[0].equals("absUrl") ? absUrl : URLs.redirect(absUrl)
							.toString();
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

			return value;
		} catch(NumberFormatException | IndexOutOfBoundsException | NullPointerException ex) {
			throw CurseException.fromThrowable(ex);
		}
	}

	public static String getValue(URL url, String data) throws CurseException {
		return getValue(get(url), data);
	}

	public static boolean isAvailable(String url) throws CurseException {
		return isAvailable(URLs.of(url));
	}

	public static boolean isAvailable(URL url) throws CurseException {
		try {
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			connection.disconnect();
		} catch(IOException ex) {
			final CurseException curseException = CurseException.fromThrowable(ex, url);
			if(!(curseException instanceof CurseUnavailableException)) {
				throw curseException;
			}
		}

		return true;
	}

	public static <E> TRLList<E> iteratePages(Object cacheKey, String baseURL,
			DocumentToList<E> documentToList, Predicate<? super E> onElementAdd, boolean threaded)
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
		final int pages = getNumberOfPages(getWithCache(baseURL + 1, cacheKey));
		final String url = baseURL;
		final Map<Integer, List<E>> allData = new ConcurrentHashMap<>();
		final IntWrapper stoppedPage = new IntWrapper(-1);

		if(threaded) {
			ThreadUtils.splitWorkload(
					CurseAPI.getMaximumThreads(),
					pages,
					page -> iteratePage(
							cacheKey,
							documentToList,
							onElementAdd,
							stoppedPage,
							url,
							allData,
							page
					)
			);
		} else {
			for(int page = 0; page < pages; page++) {
				iteratePage(cacheKey, documentToList, onElementAdd, stoppedPage, url, allData,
						page);
			}
		}

		final TRLList<E> sortedList = new TRLList<>(allData.size() * CurseAPI.RELATIONS_PER_PAGE);

		for(int i = 0; i < allData.size(); i++) {
			sortedList.addAll(allData.get(i));
		}

		return sortedList.toImmutableList();
	}

	private static <E> void iteratePage(Object cacheKey, DocumentToList<E> documentToList,
			Predicate<? super E> onElementAdd, IntWrapper stoppedPage, String url,
			Map<Integer, List<E>> allData, int page) throws CurseException {
		try {
			if(stoppedPage.get() != -1 && page > stoppedPage.get()) {
				return;
			}

			final TRLList<E> data = new TRLList<>(CurseAPI.RELATIONS_PER_PAGE);

			if(onElementAdd != null) {
				data.setOnAdd(element -> {
					if(!onElementAdd.test(element)) {
						stoppedPage.set(page);
					}
				});
			}

			allData.put(page, data);

			documentToList.documentToList(getWithCache(url + (page + 1), cacheKey), data);
		} catch(IndexOutOfBoundsException | NullPointerException | NumberFormatException ex) {
			throw CurseException.fromThrowable(ex);
		}
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
			throw CurseException.fromThrowable(ex);
		}
	}

	public static void putTemporaryCache(Object key, Map<URL, WeakReference<Document>> cache) {
		Documents.cache.put(key, cache);
	}

	public static void removeTemporaryCache(Object key) {
		cache.remove(key);
	}
}
