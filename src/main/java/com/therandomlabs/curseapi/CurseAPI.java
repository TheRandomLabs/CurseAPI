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
import static com.therandomlabs.utils.logging.Logging.getLogger;

public final class CurseAPI {
	public static final int MIN_PROJECT_ID = 10;
	public static final int MIN_FILE_ID = 1000;
	public static final int RELATIONS_PER_PAGE = 20;

	public static final String PLACEHOLDER_THUMBNAIL_URL_STRING =
			"https://media-elerium.cursecdn.com/avatars/0/93/635227964539626926.png";
	public static final URL PLACEHOLDER_THUMBNAIL_URL;

	private static BufferedImage placeholderThumbnail;

	private static int threads = Runtime.getRuntime().availableProcessors() + 2;

	private static int maxRetries = 5;
	private static int retryTime = 4;

	private static boolean widgetAPI;
	private static boolean curseMeta;

	static {
		URL url = null;

		try {
			url = new URL(PLACEHOLDER_THUMBNAIL_URL_STRING);
		} catch(MalformedURLException ex) {
			//This will never happen
		}

		PLACEHOLDER_THUMBNAIL_URL = url;

		try {
			//Initialize CurseAPIMinecraft

			final Class<?> caMinecraft =
					Class.forName("com.therandomlabs.curseapi.minecraft.CurseAPIMinecraft");

			caMinecraft.getDeclaredField("LIGHTCHOCOLATE_ID").get(null);
		} catch(NoSuchFieldException | IllegalAccessException ex) {
			getLogger().printStackTrace(ex);
		} catch(ClassNotFoundException ignored) {}
	}

	private CurseAPI() {}

	public static BufferedImage getPlaceholderThumbnail() throws IOException {
		if(placeholderThumbnail == null) {
			placeholderThumbnail = ImageIO.read(NetUtils.getInputStream(PLACEHOLDER_THUMBNAIL_URL));
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

	public static <T extends Throwable> void doWithRetries(
			RunnableWithThrowable<T> runnable) throws T {
		try {
			actuallyDoWithRetries(runnable);
		} catch(InterruptedException ex) {
			ThrowableHandling.handle(ex);
		}
	}

	public static void clearAllCache() {
		placeholderThumbnail = null;

		CurseMeta.clearFileCache();
		CurseProject.clearProjectCache();
		URLs.clearRedirectionCache();
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void actuallyDoWithRetries(
			RunnableWithThrowable<T> runnable) throws T, InterruptedException {
		for(int i = 0; i < maxRetries; i++) {
			try {
				runnable.run();
				break;
			} catch(Throwable throwable) {
				if(i == maxRetries - 1) {
					throw (T) throwable;
				}

				ThrowableHandling.handleWithoutExit(throwable);
				Thread.sleep(retryTime * 1000L);
			}
		}
	}
}
