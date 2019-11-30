package com.therandomlabs.curseapi.forgesvc;

import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface ForgeSVC {
	@GET("api/v2/addon/{id}")
	Call<ForgeSVCProject> getProject(@Path("id") int id);

	@GET("api/v2/addon/{projectID}/description")
	Call<ResponseBody> getDescription(@Path("projectID") int projectID);

	@GET("api/v2/addon/{projectID}/files")
	Call<Set<ForgeSVCFile>> getFiles(@Path("projectID") int projectID);

	@GET("api/v2/addon/{projectID}/file/{fileID}")
	Call<ForgeSVCFile> getFile(@Path("projectID") int projectID, @Path("fileID") int fileID);

	@GET("api/v2/addon/{projectID}/file/{fileID}/changelog")
	Call<ResponseBody> getChangelog(@Path("projectID") int projectID, @Path("fileID") int fileID);
}
