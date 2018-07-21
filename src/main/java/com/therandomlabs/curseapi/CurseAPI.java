package com.therandomlabs.curseapi;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import com.therandomlabs.curseapi.cursemeta.CurseMeta;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.URLs;
import com.therandomlabs.utils.io.NetUtils;
import com.therandomlabs.utils.misc.Assertions;
import com.therandomlabs.utils.runnable.RunnableWithThrowable;
import com.therandomlabs.utils.throwable.ThrowableHandling;

public final class CurseAPI {
	public static final int MIN_PROJECT_ID = 10;
	public static final int MIN_FILE_ID = 1000;
	public static final int RELATIONS_PER_PAGE = 20;

	public static final String PLACEHOLDER_THUMBNAIL_URL_STRING =
			"https://media-elerium.cursecdn.com/avatars/0/93/635227964539626926.png";
	public static final URL PLACEHOLDER_THUMBNAIL_URL;

	private static BufferedImage placeholderThumbnail;

	private static int threads = Runtime.getRuntime().availableProcessors() * 2;

	private static int maxRetries = 3;
	private static int retryTime = 5;

	private static boolean widgetAPI;
	private static boolean curseMeta = true;

	static {
		URL url = null;

		try {
			url = new URL(PLACEHOLDER_THUMBNAIL_URL_STRING);
		} catch(MalformedURLException ex) {
			//This will never happen
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

	public static boolean isWidgetAPIEnabled() {
		return widgetAPI;
	}

	public static void setWidgetAPIEnabled(boolean flag) {
		widgetAPI = flag;
	}

	public static boolean isCurseMetaEnabled() {
		return curseMeta;
	}

	public static void setCurseMetaEnabled(boolean flag) {
		curseMeta = flag;
	}

	public static void disableCurseMetaIfNecessary() {
		if(!CurseMeta.isAvailable()) {
			curseMeta = false;
		}
	}

	public static boolean isValidProjectID(int id) {
		return id >= CurseAPI.MIN_PROJECT_ID;
	}

	public static void validateProjectID(int id) {
		if(!isValidProjectID(id)) {
			throw new IllegalArgumentException("Invalid Curse project ID: " + id);
		}
	}

	public static boolean isValidFileID(int id) {
		return id >= CurseAPI.MIN_FILE_ID;
	}

	public static void validateFileID(int id) {
		if(!isValidFileID(id)) {
			throw new IllegalArgumentException("Invalid Curse file ID: " + id);
		}
	}

	public static void doWithRetries(RunnableWithThrowable<CurseException> runnable)
			throws CurseException {
		try {
			for(int i = 0; i < maxRetries; i++) {
				try {
					runnable.run();
					break;
				} catch(CurseException ex) {
					if(i == maxRetries - 1) {
						throw ex;
					}

					ThrowableHandling.handleWithoutExit(ex);
					Thread.sleep(retryTime * 1000L);
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
		URLs.clearRedirectionCache();
	}
}
