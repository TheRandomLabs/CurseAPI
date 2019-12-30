package com.therandomlabs.curseapi.util;

import java.io.IOException;

import com.therandomlabs.curseapi.CurseException;
import okhttp3.ResponseBody;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Contains utility methods for working with Retrofit.
 */
public final class RetrofitUtils {
	private static final Logger logger = LoggerFactory.getLogger(RetrofitUtils.class);

	private RetrofitUtils() {}

	/**
	 * Returns a {@link Retrofit} instance for the specified base URL that uses
	 * {@link MoshiUtils#moshi}.
	 *
	 * @param baseURL a URL.
	 * @return a {@link Retrofit} instance.
	 */
	public static Retrofit get(String baseURL) {
		return new Retrofit.Builder().
				baseUrl(baseURL).
				addConverterFactory(MoshiConverterFactory.create(MoshiUtils.moshi)).
				build();
	}

	/**
	 * Executes the specified {@link Call}, and if it fails, throws a {@link CurseException}
	 * with an appropriate detail message.
	 *
	 * @param call a {@link Call}.
	 * @param <T> the response body type of the {@link Call}.
	 * @return the deserialized response body.
	 * @throws CurseException if the {@link Call} fails to execute correctly.
	 */
	@SuppressWarnings("GrazieInspection")
	public static <T> T execute(Call<T> call) throws CurseException {
		logger.debug("Executing request: {}", call.request());

		try {
			final Response<T> response = call.execute();

			if (response.isSuccessful()) {
				return response.body();
			}

			//The path could not be found, so we return null.
			if (response.code() == 404) {
				return null;
			}

			throw new CurseException(String.format(
					"Failed to execute call. HTTP status: %s (%s). Response body: %s",
					response.message(), response.code(), response.errorBody().string()
			));
		} catch (IOException ex) {
			throw new CurseException(ex);
		}
	}

	/**
	 * Executes the specified {@link Call} using {@link #execute(Call)} and returns the response
	 * body as a string.
	 *
	 * @param call a {@link Call}.
	 * @return the response body as a string.
	 * @throws CurseException if the {@link Call} fails to execute correctly.
	 */
	public static String getString(Call<ResponseBody> call) throws CurseException {
		try {
			final ResponseBody responseBody = execute(call);
			return responseBody == null ? null : responseBody.string();
		} catch (IOException ex) {
			throw new CurseException(ex);
		}
	}

	/**
	 * Executes the specified {@link Call} using {@link #execute(Call)} and returns the response
	 * body as an {@link Element}.
	 *
	 * @param call a {@link Call}.
	 * This is different from when the {@link Call} fails to execute, in which case
	 * {@code null} is returned as usual.
	 * @return the response body as an {@link Element}, or an empty {@link Element} if it is empty.
	 * @throws CurseException if the {@link Call} fails to execute correctly.
	 * @see JsoupUtils#emptyElement()
	 */
	public static Element getElement(Call<ResponseBody> call) throws CurseException {
		final String string = getString(call);

		if (string == null) {
			return null;
		}

		final Element element = JsoupUtils.parseBody(string);
		return element == null ? JsoupUtils.emptyElement() : element;
	}
}
