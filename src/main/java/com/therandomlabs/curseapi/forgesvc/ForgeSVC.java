package com.therandomlabs.curseapi.forgesvc;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface ForgeSVC {
	@GET("api/v2/addon/{id}")
	Call<ForgeSVCProject> getProject(@Path("id") int id);
}
