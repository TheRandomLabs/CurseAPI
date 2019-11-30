package com.therandomlabs.curseapi.forgesvc;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface ForgeSVC {
	@GET("api/v2/addon/{id}")
	Call<ForgeSVCProject> getProject(@Path("id") int id);

	@GET("api/v2/addon/{id}/description")
	Call<ResponseBody> getDescription(@Path("id") int id);

	@GET("api/v2/addon/{projectID}/file/{fileID}/changelog")
	Call<ResponseBody> getChangelog(@Path("projectID") int projectID, @Path("fileID") int fileID);
}
