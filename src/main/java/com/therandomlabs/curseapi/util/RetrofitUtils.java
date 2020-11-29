/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2020 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.therandomlabs.curseapi.util;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.squareup.moshi.JsonDataException;
import com.therandomlabs.curseapi.CurseException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retrofit.CircuitBreakerCallAdapter;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.control.Try;
import okhttp3.ResponseBody;
import org.checkerframework.checker.nullness.qual.Nullable;
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

	private static final CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("curseapi");

	private static Function<String, Retrofit> retrofitSupplier = baseURL -> new Retrofit.Builder().
			addCallAdapterFactory(CircuitBreakerCallAdapter.of(
					circuitBreaker,
					response -> response.isSuccessful() || response.code() == 404
			)).
			baseUrl(baseURL).
			client(OkHttpUtils.getClient()).
			addConverterFactory(MoshiConverterFactory.create(MoshiUtils.moshi)).
			build();

	private static Retry retry = Retry.ofDefaults("curseapi");

	private RetrofitUtils() {}

	/**
	 * Returns a {@link Retrofit} instance for the specified base URL that uses
	 * {@link MoshiUtils#moshi}.
	 *
	 * @param baseURL a URL.
	 * @return a {@link Retrofit} instance.
	 */
	public static Retrofit get(String baseURL) {
		Preconditions.checkNotNull(baseURL, "baseURL should not be null");
		return retrofitSupplier.apply(baseURL);
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
	@Nullable
	public static <T> T execute(Call<T> call) throws CurseException {
		Preconditions.checkNotNull(call, "call should not be null");

		logger.debug("Executing request: {}", call.request());

		try {
			final Response<T> response = Try.of(Retry.decorateCheckedSupplier(
					retry, () -> call.clone().execute()
			)).get();

			if (response.isSuccessful()) {
				return response.body();
			}

			//The path could not be found, so we return null.
			if (response.code() == 400 || response.code() == 404) {
				return null;
			}

			try (ResponseBody errorBody = response.errorBody()) {
				throw new CurseException(String.format(
						"Failed to execute call. HTTP status: %s (%s). Response body: %s",
						response.message(), response.code(),
						errorBody == null ? null : errorBody.string()
				));
			}
		} catch (IOException | JsonDataException ex) {
			throw new CurseException("Failed to execute request: " + call.request(), ex);
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
	@Nullable
	public static String getString(Call<ResponseBody> call) throws CurseException {
		Preconditions.checkNotNull(call, "call should not be null");

		try {
			final ResponseBody responseBody = execute(call);
			return responseBody == null ? null : responseBody.string();
		} catch (IOException ex) {
			throw new CurseException("Failed to get string: " + call.request(), ex);
		}
	}

	/**
	 * Executes the specified {@link Call} using {@link #execute(Call)} and returns the response
	 * body as an {@link Element}.
	 *
	 * @param call a {@link Call}.
	 * @return the response body as an {@link Element}, or an empty {@link Element} if it is empty.
	 * This is different from when the {@link Call} fails to execute, in which case
	 * {@code null} is returned as usual.
	 * @throws CurseException if the {@link Call} fails to execute correctly.
	 * @see JsoupUtils#emptyElement()
	 */
	@Nullable
	public static Element getElement(Call<ResponseBody> call) throws CurseException {
		Preconditions.checkNotNull(call, "call should not be null");

		final String string = getString(call);

		if (string == null) {
			return null;
		}

		return JsoupUtils.parseBody(string);
	}

	/**
	 * Sets CurseAPI's {@link Retrofit} supplier.
	 *
	 * @param supplier a {@link Function} that returns a {@link Retrofit} instance for
	 * a given base URL.
	 */
	public static void setRetrofitSupplier(Function<String, Retrofit> supplier) {
		Preconditions.checkNotNull(supplier, "supplier should not be null");
		retrofitSupplier = supplier;
	}

	/**
	 * Sets CurseAPI's retry configuration.
	 *
	 * @param waitDuration the wait duration between retries.
	 * @param maxAttempts the maximum number of retries.
	 */
	public static void setRetryConfig(Duration waitDuration, int maxAttempts) {
		Preconditions.checkArgument(waitDuration.toMillis() > 0, "waitDuration should be positive");
		Preconditions.checkArgument(maxAttempts > 0, "maxAttempts should be positive");
		retry = Retry.of(
				"curseapi",
				RetryConfig.custom().
						waitDuration(waitDuration).
						maxAttempts(maxAttempts).
						build()
		);
	}
}
