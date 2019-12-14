package com.therandomlabs.curseapi.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.BufferedSink;
import okio.Okio;
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
	 * @throws CurseException if the request cannot be executed correctly.
	 */
	public static BufferedImage readImage(HttpUrl url) throws CurseException {
		Preconditions.checkNotNull(url, "url should not be null");

		final Request request = new Request.Builder().url(url).build();
		logger.info("Executing request: {}", request);

		try {
			return ImageIO.read(client.newCall(request).execute().body().byteStream());
		} catch (IOException ex) {
			throw new CurseException(ex);
		}
	}

	/**
	 * Downloads a file from the specified {@link HttpUrl} to the specified {@link Path}.
	 *
	 * @param url an {@link HttpUrl}.
	 * @param path a {@link Path}.
	 * @throws CurseException if the request cannot be executed correctly or if an I/O error occurs.
	 */
	public static void download(HttpUrl url, Path path) throws CurseException {
		Preconditions.checkNotNull(url, "url should not be null");
		Preconditions.checkNotNull(path, "path should not be null");

		final Request request = new Request.Builder().url(url).build();
		logger.info("Executing request: {}", request);

		try (BufferedSink sink = Okio.buffer(Okio.sink(path))) {
			sink.writeAll(client.newCall(request).execute().body().source());
		} catch (IOException ex) {
			throw new CurseException(ex);
		}
	}

	/**
	 * Downloads a file from the specified {@link HttpUrl} to the specified directory with
	 * the specified file name.
	 *
	 * @param url an {@link HttpUrl}.
	 * @param directory a {@link Path} to a directory. If the directory does not exist,
	 * it is created.
	 * @param fileName a file name.
	 * @return a {@link Path} to the downloaded file.
	 * @throws CurseException if the request cannot be executed correctly or if an I/O error occurs.
	 */
	public static Path downloadToDirectory(HttpUrl url, Path directory, String fileName)
			throws CurseException {
		Preconditions.checkNotNull(url, "url should not be null");
		Preconditions.checkNotNull(directory, "directory should not be null");
		Preconditions.checkArgument(
				!Files.isRegularFile(directory), "directory should not be a regular file"
		);
		Preconditions.checkNotNull(fileName, "fileName should not be null");

		try {
			Files.createDirectories(directory);
			final Path path = directory.resolve(fileName);
			download(url, path);
			return path;
		} catch (IOException ex) {
			throw new CurseException(ex);
		}
	}

	/**
	 * Gets the file name embedded within the path of the specified URL.
	 * @param url a URL.
	 * @return the file name embedded within the path of the specified URL.
	 */
	public static String getFileNameFromURLPath(HttpUrl url) {
		Preconditions.checkNotNull(url, "url should not be null");

		final List<String> pathSegments = url.encodedPathSegments();
		//TODO does CurseForge still put tabs in their file names?
		final String path = pathSegments.get(pathSegments.size() - 1).replace('\t', ' ');

		try {
			return URLDecoder.decode(path, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException(
					"UTF-8 encoding is not supported; this should never happen", ex
			);
		}
	}
}
