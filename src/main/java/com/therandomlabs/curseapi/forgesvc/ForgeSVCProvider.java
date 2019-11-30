package com.therandomlabs.curseapi.forgesvc;

import java.io.IOException;

import com.squareup.moshi.Moshi;
import com.therandomlabs.curseapi.CurseAPIProvider;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.HttpUrlAdapter;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public final class ForgeSVCProvider implements CurseAPIProvider {
	public static final ForgeSVCProvider INSTANCE = new ForgeSVCProvider();

	static final ForgeSVC forgeSVC = new Retrofit.Builder().
			baseUrl("https://addons-ecs.forgesvc.net/").
			addConverterFactory(MoshiConverterFactory.create(
					new Moshi.Builder().
							add(HttpUrlAdapter.INSTANCE).
							build()
			)).
			build().
			create(ForgeSVC.class);

	private ForgeSVCProvider() {}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseProject project(int id) throws CurseException {
		return execute(forgeSVC.getProject(id));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurseFile file(int projectID, int fileID) {
		return null;
	}

	static <T> T execute(Call<T> call) throws CurseException {
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
}
