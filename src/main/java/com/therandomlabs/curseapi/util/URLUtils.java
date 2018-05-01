package com.therandomlabs.curseapi.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.utils.io.NetUtils;

public final class URLUtils {
	//Curse usually redirects to URLs with "cookieTest=" at the end of them.
	public static final Pattern COOKIE_TEST =
			Pattern.compile("\\?cookieTest=*(?=&)|\\?cookieTest=[^&]*");

	private static final Map<String, String> redirectionCache = new ConcurrentHashMap<>(50);

	private URLUtils() {}

	public static URL redirect(String url) throws CurseException {
		return redirect(URLUtils.url(url));
	}

	public static URL redirect(URL url) throws CurseException {
		final String urlString = url.toString();

		if(redirectionCache.containsKey(urlString)) {
			return URLUtils.url(redirectionCache.get(urlString));
		}

		try {
			CurseEventHandling.forEach(eventHandler -> eventHandler.preRedirect(urlString));

			final URL redirected = stripCookieTestString(
					NetUtils.getRedirectedURL(url, NetUtils.DEFAULT_REDIRECTIONS, false));
			final String redirectedString = redirected.toString();

			CurseEventHandling.forEach(eventHandler ->
					eventHandler.postRedirect(urlString, redirectedString));

			redirectionCache.put(urlString, redirectedString);

			return redirected;
		} catch(IOException ex) {
			throw CurseException.fromThrowable(ex);
		}
	}

	public static URL url(String url) throws CurseException {
		try {
			return new URL(url);
		} catch(MalformedURLException ex) {
			throw CurseException.fromThrowable(ex);
		}
	}

	public static URL stripCookieTestString(URL url) throws CurseException {
		return URLUtils.url(stripCookieTestString(url.toString()));
	}

	public static String stripCookieTestString(String string) {
		return COOKIE_TEST.matcher(string).replaceAll("");
	}

	public static void clearRedirectionCache() {
		redirectionCache.clear();
	}
}
