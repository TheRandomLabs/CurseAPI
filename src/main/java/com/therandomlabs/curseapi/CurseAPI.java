package com.therandomlabs.curseapi;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import com.therandomlabs.curseapi.cursemeta.CurseMeta;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.curseapi.widget.WidgetAPI;
import com.therandomlabs.utils.io.NetUtils;
import com.therandomlabs.utils.misc.Assertions;
import com.therandomlabs.utils.runnable.RunnableWithThrowable;
import com.therandomlabs.utils.throwable.ThrowableHandling;

public final class CurseAPI {
	public static final int MIN_PROJECT_ID = 10;
	public static final int RELATIONS_PER_PAGE = 20;

	public static final String PLACEHOLDER_THUMBNAIL_URL_STRING =
			"https://media-elerium.cursecdn.com/avatars/0/93/635227964539626926.png";
	public static final URL PLACEHOLDER_THUMBNAIL_URL;

	private static BufferedImage placeholderThumbnail;

	private static int threads = Runtime.getRuntime().availableProcessors() * 2;

	private static int maxRetries = 5;
	private static int retryTime = 5;

	private static boolean avoidWidgetAPI = true;
	private static boolean avoidCurseMeta;

	static {
		URL url = null;

		try {
			url = new URL(PLACEHOLDER_THUMBNAIL_URL_STRING);
		} catch(MalformedURLException ex) {
			ThrowableHandling.handle(ex);
		}

		PLACEHOLDER_THUMBNAIL_URL = url;
	}

	private CurseAPI() {}

	public static BufferedImage getPlaceholderThumbnail() throws IOException {
		if(placeholderThumbnail == null) {
			placeholderThumbnail = ImageIO.read(NetUtils.download(PLACEHOLDER_THUMBNAIL_URL));
		}

		return placeholderThumbnail;
	}

	public static int getMaximumThreads() {
		return threads;
	}

	public static void setMaximumThreads(int threads) {
		Assertions.positive(threads, "threads", false);
		CurseAPI.threads = threads;
	}

	public static int getMaximumRetries() {
		return maxRetries;
	}

	public static void setMaximumRetries(int retries) {
		Assertions.positive(retries, "retries", false);
		maxRetries = retries;
	}

	public static int getRetryTime() {
		return retryTime;
	}

	public static void setRetryTime(int time) {
		retryTime = time;
	}

	public static boolean isAvoidingWidgetAPI() {
		return avoidWidgetAPI;
	}

	public static void avoidWidgetAPI(boolean flag) {
		avoidWidgetAPI = flag;
	}

	public static boolean isAvoidingCurseMeta() {
		return avoidCurseMeta;
	}

	public static void avoidCurseMeta(boolean flag) {
		avoidCurseMeta = flag;
	}

	public static void doWithRetries(RunnableWithThrowable<CurseException> runnable)
			throws CurseException {
		try {
			for(int i = 0; i < maxRetries; i++) {
				try {
					runnable.run();
					Thread.sleep(retryTime * 1000L);
					break;
				} catch(CurseException ex) {
					if(i == maxRetries - 1) {
						throw ex;
					}
					ThrowableHandling.handleWithoutExit(ex);
				}
			}
		} catch(InterruptedException ex) {
			ThrowableHandling.handle(ex);
		}
	}

	public static void clearAllCache() {
		placeholderThumbnail = null;

		CurseMeta.clearCache();
		CurseProject.clearProjectCache();
		DocumentUtils.clearCache();
		URLUtils.clearRedirectionCache();
		WidgetAPI.clearCache();

		try {
			Class.forName("com.therandomlabs.curseapi.minecraft.CurseAPIMinecraft").
					getDeclaredMethod("clearAllCache").invoke(null);
		} catch(Exception ex) {
			if(!(ex instanceof ClassNotFoundException)) {
				ThrowableHandling.handle(ex);
			}
		}
	}
}
