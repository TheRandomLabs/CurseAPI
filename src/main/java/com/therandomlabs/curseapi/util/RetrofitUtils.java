package com.therandomlabs.curseapi.util;

import java.io.IOException;

import com.squareup.moshi.Moshi;
import com.therandomlabs.curseapi.CurseException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public final class RetrofitUtils {
	public static final Retrofit RETROFIT = new Retrofit.Builder().
			baseUrl("https://addons-ecs.forgesvc.net/").
			addConverterFactory(MoshiConverterFactory.create(
					new Moshi.Builder().
							add(HttpUrlAdapter.INSTANCE).
							build()
			)).
			build();

	private RetrofitUtils() {}

	public static <T> T create(Class<T> service) {
		return RETROFIT.create(service);
	}

	public static <T> T execute(Call<T> call) throws CurseException {
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
					"Failed to execute call. HTTP status %s (%s). Response body: %s",
					response.message(), response.code(), response.errorBody().string()
			));
		} catch (IOException ex) {
			throw new CurseException(ex);
		}
	}

	public static String getString(Call<ResponseBody> call) throws CurseException {
		try {
			return execute(call).string();
		} catch (IOException ex) {
			throw new CurseException(ex);
		}
	}
}
