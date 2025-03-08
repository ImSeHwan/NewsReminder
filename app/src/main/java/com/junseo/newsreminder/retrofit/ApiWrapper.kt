package com.junseo.newsreminder.retrofit

import retrofit2.Response
import java.io.IOException

object ApiWrapper {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(IOException("Response body is null"))
            } else {
                Result.failure(IOException("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("Network request failed: ${e.localizedMessage}", e))
        }
    }
}
