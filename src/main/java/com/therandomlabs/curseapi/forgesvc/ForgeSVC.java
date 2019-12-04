package com.therandomlabs.curseapi.forgesvc;

import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface ForgeSVC {
	@GET("api/v2/addon/{id}")
	Call<ForgeSVCProject> getProject(@Path("id") int id);

	@GET("api/v2/addon/search")
	Call<List<ForgeSVCProject>> searchProjects(
			@Query("gameId") int gameID, @Query("sectionId") int categorySectionID,
			@Query("categoryId") int categoryID, @Query("gameVersion") String gameVersion,
			@Query("index") int pageIndex, @Query("pageSize") int pageSize,
			@Query("searchFilter") String searchFilter, @Query("sort") int sortingMethod
	);

	@GET("api/v2/addon/{projectID}/description")
	Call<ResponseBody> getDescription(@Path("projectID") int projectID);

	@GET("api/v2/addon/{projectID}/files")
	Call<Set<ForgeSVCFile>> getFiles(@Path("projectID") int projectID);

	@GET("api/v2/addon/{projectID}/file/{fileID}")
	Call<ForgeSVCFile> getFile(@Path("projectID") int projectID, @Path("fileID") int fileID);

	@GET("api/v2/addon/{projectID}/file/{fileID}/changelog")
	Call<ResponseBody> getChangelog(@Path("projectID") int projectID, @Path("fileID") int fileID);

	@GET("api/v2/addon/{projectID}/file/{fileID}/download-url")
	Call<ResponseBody> getFileDownloadURL(
			@Path("projectID") int projectID, @Path("fileID") int fileID
	);

	@GET("api/v2/game")
	Call<Set<ForgeSVCGame>> getGames(@Query("supportsAddons") boolean requireAddonSupport);

	@GET("api/v2/category/section/{sectionID}")
	Call<Set<ForgeSVCCategory>> getCategories(@Path("sectionID") int sectionID);
}
