package com.therandomlabs.curseapi.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains utility methods for working with OkHttp.
 */
public final class OkHttpUtils {
	private static final Logger logger = LoggerFactory.getLogger(OkHttpUtils.class);
	private static final OkHttpClient client = new OkHttpClient();

	private OkHttpUtils() {}

	/**
	 * Reads a {@link BufferedImage} from the specified URL.
	 *
	 * @param url an image URL.
	 * @return a {@link BufferedImage} read from the specified URL.
	 * @throws CurseException if the request could not be executed correctly.
	 */
	public static BufferedImage readImage(HttpUrl url) throws CurseException {
		Preconditions.checkNotNull(url, "url should not be null");

		try {
			final Request request = new Request.Builder().url(url).build();
			logger.info("Executing request: {}", request);
			return ImageIO.read(client.newCall(request).execute().body().byteStream());
		} catch (IOException ex) {
			throw new CurseException(ex);
		}
	}
}
