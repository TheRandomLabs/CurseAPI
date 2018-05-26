package com.therandomlabs.curseapi.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import com.therandomlabs.curseapi.CurseEventHandling;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.utils.io.NetUtils;

public final class URLs {
	//Curse usually redirects to URLs with "cookieTest=" at the end of them.
	public static final Pattern COOKIE_TEST =
			Pattern.compile("\\?cookieTest=(.*(?=&)|[^&]*)");

	private static final Map<URL, URL> redirectionCache = new ConcurrentHashMap<>(50);

	private URLs() {}

	public static URL redirect(String url) throws CurseException {
		return redirect(URLs.url(url));
	}

	public static URL redirect(URL url) throws CurseException {
		if(redirectionCache.containsKey(url)) {
			return redirectionCache.get(url);
		}

		try {
			CurseEventHandling.forEach(eventHandler -> eventHandler.preRedirect(url));

			final URL redirected = stripCookieTestString(
					NetUtils.getRedirectedURL(url, NetUtils.DEFAULT_REDIRECTIONS, false));

			CurseEventHandling.forEach(eventHandler -> eventHandler.postRedirect(url, redirected));

			redirectionCache.put(url, redirected);

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
		return URLs.url(stripCookieTestString(url.toString()));
	}

	public static String stripCookieTestString(String string) {
		return COOKIE_TEST.matcher(string).replaceAll("");
	}

	public static void clearRedirectionCache() {
		redirectionCache.clear();
	}
}
