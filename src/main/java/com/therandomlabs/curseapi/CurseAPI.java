package com.therandomlabs.curseapi;

import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.curseapi.widget.WidgetAPI;
import com.therandomlabs.utils.misc.Assertions;
import com.therandomlabs.utils.throwable.ThrowableHandling;

public final class CurseAPI {
	public static final int MIN_PROJECT_ID = 10;

	private static int threads = Runtime.getRuntime().availableProcessors() * 2;
	private static int maxRetries = 5;

	private CurseAPI() {}

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

	public static void clearAllCache() {
		CurseProject.clearProjectCache();
		DocumentUtils.clearCache();
		URLUtils.clearRedirectionCache();
		WidgetAPI.clearCache();

		try {
			Class.forName("com.therandomlabs.curseapi.minecraft").
					getDeclaredMethod("clearAllCache").
					invoke(null);
		} catch(Exception ex) {
			if(!(ex instanceof ClassNotFoundException)) {
				ThrowableHandling.handleUnexpected(ex);
			}
		}
	}
}
