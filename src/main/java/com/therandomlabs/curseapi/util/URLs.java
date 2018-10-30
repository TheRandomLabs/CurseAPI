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
	public static final Pattern COOKIE_TEST = Pattern.compile("\\?cookieTest=(.*(?=&)|[^&]*)");

	private static final Map<String, String> redirectionCache = new ConcurrentHashMap<>(50);

	private URLs() {}

	public static URL redirect(String url) throws CurseException {
		return redirect(of(url));
	}

	public static URL redirect(URL url) throws CurseException {
		final String urlString = url.toString();

		if(redirectionCache.containsKey(urlString)) {
			return URLs.of(redirectionCache.get(urlString));
		}

		try {
			CurseEventHandling.forEach(eventHandler -> eventHandler.preRedirect(url));

			final URL redirected = stripCookieTestString(NetUtils.getRedirectedURL(
					url, NetUtils.DEFAULT_REDIRECTIONS, false
			));

			CurseEventHandling.forEach(eventHandler -> eventHandler.postRedirect(url, redirected));

			redirectionCache.put(urlString, redirected.toString());

			return redirected;
		} catch(IOException ex) {
			throw CurseException.fromThrowable(ex, url);
		}
	}

	public static URL of(String url) throws CurseException {
		try {
			return new URL(url);
		} catch(MalformedURLException ex) {
			throw CurseException.fromThrowable(ex);
		}
	}

	public static URL stripCookieTestString(URL url) throws CurseException {
		return of(stripCookieTestString(url.toString()));
	}

	public static String stripCookieTestString(String string) {
		return COOKIE_TEST.matcher(string).replaceAll("");
	}

	public static void clearRedirectionCache() {
		redirectionCache.clear();
	}
}
