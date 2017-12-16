package com.therandomlabs.curseapi.util;

import static com.therandomlabs.utils.logging.Logging.getLogger;
import java.util.ArrayList;
import java.util.List;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.minecraft.modpack.ModInfo;
import com.therandomlabs.utils.runnable.RunnableWithInputAndThrowable;
import com.therandomlabs.utils.wrapper.Wrapper;

public final class CurseEventHandling {
	public static final CurseEventHandler DEFAULT_EVENT_HANDLER = new DefaultCurseEventHandler();
	private static final List<CurseEventHandler> eventHandlers = new ArrayList<>(5);

	public static class DefaultCurseEventHandler implements CurseEventHandler {
		@Override
		public void preRedirect(String url) {
			getLogger().debug("Redirecting URL: " + url);
		}

		@Override
		public void postRedirect(String originalURL, String redirectedURL) {
			getLogger().debug("%s redirected to: %s", originalURL, redirectedURL);
		}

		@Override
		public void preDownloadDocument(String url) {
			getLogger().debug("Downloading document: " + url);
		}

		@Override
		public void postDownloadDocument(String url) {
			getLogger().debug("Downloaded document: "+ url);
		}

		@Override
		public void retryingJSON(int retryingIn) {
			getLogger().debug("Failed to retrieve JSON. Retrying in %s seconds...", retryingIn);
		}

		@Override
		public void autosavedInstallerData() {
			getLogger().debug("Autosaved installer data.");
		}

		@Override
		public void deleting(String fileName) {
			getLogger().info("Deleting: " + fileName);
		}

		@Override
		public void copying(String fileName) {
			getLogger().info("Copying: " + fileName);
		}

		@Override
		public void downloadingFile(String fileName) {
			getLogger().info("Downloading: " + fileName);
		}

		@Override
		public void extracting(String fileName) {
			getLogger().info("Extracting: " + fileName);
		}

		@Override
		public void downloadingMod(String modName, int count, int total) {
			if(modName.equals(ModInfo.UNKNOWN_TITLE)) {
				getLogger().info("Downloading mod %s of %s...", count, total, modName);
			} else {
				getLogger().info("Downloading mod %s of %s: %s", count, total, modName);
			}

			getLogger().flush();
		}

		@Override
		public void downloadedMod(String modName, String fileName, int count) {
			if(modName.equals(ModInfo.UNKNOWN_TITLE)) {
				getLogger().info("Downloaded mod %s: %s", modName, fileName);
			} else {
				getLogger().info("Downloaded mod: " + fileName);
			}

			getLogger().flush();
		}

		@Override
		public void installingForge(String forgeVersion) {
			getLogger().info("Installing Forge %s...", forgeVersion);
		}
	}

	private CurseEventHandling() {}

	static {
		register(DEFAULT_EVENT_HANDLER);
	}

	public static void register(CurseEventHandler eventHandler) {
		eventHandlers.add(eventHandler);
	}

	public static void unregister(CurseEventHandler eventHandler) {
		eventHandlers.remove(eventHandler);
	}

	public static void forEach(
			RunnableWithInputAndThrowable<CurseEventHandler, CurseException> consumer)
			throws CurseException {
		final Wrapper<CurseException> exception = new Wrapper<>();

		for(CurseEventHandler eventHandler : eventHandlers) {
			try {
				consumer.run(eventHandler);
			} catch(CurseException ex) {
				exception.set(ex);
			}
		}

		if(exception.hasValue()) {
			throw exception.get();
		}
	}
}
