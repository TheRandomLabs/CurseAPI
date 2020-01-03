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

package com.therandomlabs.curseapi.forgesvc;

import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface ForgeSvc {
	@GET("api/v2/addon/{projectID}")
	Call<ForgeSvcProject> getProject(@Path("projectID") int id);

	@GET("api/v2/addon/search")
	Call<List<ForgeSvcProject>> searchProjects(
			@Query("gameId") int gameID, @Query("sectionId") int categorySectionID,
			@Query("categoryId") int categoryID, @Query("gameVersion") String gameVersionString,
			@Query("index") int pageIndex, @Query("pageSize") int pageSize,
			@Query("searchFilter") String searchFilter, @Query("sort") int sortingMethod
	);

	@GET("api/v2/addon/{projectID}/description")
	Call<ResponseBody> getDescription(@Path("projectID") int projectID);

	@GET("api/v2/addon/{projectID}/files")
	Call<Set<ForgeSvcFile>> getFiles(@Path("projectID") int projectID);

	@GET("api/v2/addon/{projectID}/file/{fileID}")
	Call<ForgeSvcFile> getFile(@Path("projectID") int projectID, @Path("fileID") int fileID);

	@GET("api/v2/addon/{projectID}/file/{fileID}/changelog")
	Call<ResponseBody> getChangelog(@Path("projectID") int projectID, @Path("fileID") int fileID);

	@GET("api/v2/addon/{projectID}/file/{fileID}/download-url")
	Call<ResponseBody> getFileDownloadURL(
			@Path("projectID") int projectID, @Path("fileID") int fileID
	);

	@GET("api/v2/game")
	Call<Set<ForgeSvcGame>> getGames(@Query("supportsAddons") boolean requireAddonSupport);

	@GET("api/v2/game/{gameID}")
	Call<ForgeSvcGame> getGame(@Path("gameID") int id);

	@GET("api/v2/category")
	Call<Set<ForgeSvcCategory>> getCategories();

	@GET("api/v2/category/section/{sectionID}")
	Call<Set<ForgeSvcCategory>> getCategories(@Path("sectionID") int sectionID);

	@GET("api/v2/category/{categoryID}")
	Call<ForgeSvcCategory> getCategory(@Path("categoryID") int id);
}
