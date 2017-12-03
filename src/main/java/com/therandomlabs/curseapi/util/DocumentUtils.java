package com.therandomlabs.curseapi.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.curseforge.CurseForge;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.collection.CollectionUtils;
import com.therandomlabs.utils.collection.TRLList;
import com.therandomlabs.utils.misc.StopSwitch;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.network.NetworkUtils;
import com.therandomlabs.utils.runnable.RunnableWithInput;
import com.therandomlabs.utils.wrapper.Wrapper;

public final class DocumentUtils {
	private static final Map<String, Document> documents = new HashMap<>(50);

	private DocumentUtils() {}

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
				String absUrl = element.attr(values[1]);

				//Too short to be a URL
				if(absUrl.length() < 5) {
					return absUrl;
				}

				try {
					new URL(absUrl);
				} catch(MalformedURLException ex) {
					if(ex.getMessage().contains("no protocol")) {
						absUrl = "https:" + absUrl;
					} else {
						throw new CurseException(ex);
					}
				}
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

		final int threadCount =
				pages < CurseAPI.getMaximumThreads() ? pages : CurseAPI.getMaximumThreads();
		final Thread[] threads = new Thread[threadCount];

		final int pageIncrement = pages / threadCount; //Rounded down by integer division

		final Map<Integer, List<E>> allData = new HashMap<>(threadCount);

		final Wrapper<CurseException> exception = new Wrapper<>();

		for(int i = 0, j = 1; i < threadCount; i++, j += pageIncrement) {
			final String url = baseURL;

			final TRLList<E> data = new TRLList<>(23);
			data.setOnAdd(onElementAdd);
			allData.put(i, data);

			threads[i] = new ThreadWithIndexValues(i, j, indexes -> {
				final int threadIndex = indexes.value1();
				final int startPage = indexes.value2();

				final int endPage;
				if(threadIndex == threadCount - 1) {
					//This means that this is the last full batch of pages, so set endPage to the
					//last page so we can do the last few pages as well
					endPage = pages + 1;
				} else {
					endPage = startPage + pageIncrement;
				}

				try {
					for(int page = startPage; page < endPage; page++) {
						if(exception.hasValue() ||
								(stopSwitch != null && stopSwitch.isStopped())) {
							return;
						}
						documentToList.documentToList(get(url + page), data);
					}
				} catch(CurseException ex) {
					exception.set(ex);
				} catch(IndexOutOfBoundsException | NullPointerException |
						NumberFormatException ex) {
					exception.set(new CurseException(ex));
				}
			});
			threads[i].start();
		}

		try {
			//Make sure all threads are complete before returning
			for(Thread thread : threads) {
				thread.join();
			}
		} catch(InterruptedException ex) {
			throw new CurseException(ex);
		}

		if(exception.hasValue()) {
			throw exception.get();
		}

		final TRLList<E> sortedList = new TRLList<>(allData.size() * 23);
		for(int i = 0; i < allData.size(); i++) {
			sortedList.addAll(allData.get(i));
		}
		return sortedList.toImmutableList();
	}

	public static void clearDocumentCache() {
		documents.clear();
	}
}
