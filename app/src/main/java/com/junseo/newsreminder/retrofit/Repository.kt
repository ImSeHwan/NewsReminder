package com.junseo.newsreminder.retrofit

import com.junseo.newsreminder.model.NaverNewsResponse

class Repository(private val apiService: ApiService) {

    suspend fun fetchData(endpoint: String, query: Map<String, String> = emptyMap()): Result<Any> {
        return ApiWrapper.safeApiCall { apiService.getRequest(endpoint, query) }
    }

    suspend fun postData(endpoint: String, body: Any): Result<Any> {
        return ApiWrapper.safeApiCall { apiService.postRequest(endpoint, body) }
    }

    suspend fun patchData(endpoint: String, body: Any): Result<Any> {
        return ApiWrapper.safeApiCall { apiService.patchRequest(endpoint, body) }
    }
    suspend fun patchNewsList(keyword: String): Result<NaverNewsResponse> {
        return ApiWrapper.safeApiCall { apiService.searchNews(keyword) }
    }

}
