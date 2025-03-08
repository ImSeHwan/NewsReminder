package com.junseo.newsreminder.retrofit

import com.junseo.newsreminder.model.NaverNewsResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("{endpoint}")
    suspend fun getRequest(
        @Path("endpoint") endpoint: String,
        @QueryMap query: Map<String, String> = emptyMap()
    ): Response<Any>

    @POST("{endpoint}")
    suspend fun postRequest(
        @Path("endpoint") endpoint: String,
        @Body body: Any
    ): Response<Any>

    @PATCH("{endpoint}")
    suspend fun patchRequest(
        @Path("endpoint") endpoint: String,
        @Body body: Any
    ): Response<Any>

    @GET("v1/search/news")
    suspend fun searchNews(
        @Query("query") query: String,     // 검색어
        @Query("display") display: Int = 10, // 검색 결과 개수 (기본값: 10)
        @Query("start") start: Int = 1,    // 검색 시작 위치
        @Query("sort") sort: String = "date" // 정렬 방식 (date: 최신순, sim: 유사도순)
    ): Response<NaverNewsResponse>
}
