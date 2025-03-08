package com.junseo.newsreminder.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://openapi.naver.com/"

    // 네이버 API 요청을 위한 인증 헤더 추가
    private val authInterceptor = Interceptor { chain ->
        val request: Request = chain.request().newBuilder()
            .addHeader("X-Naver-Client-Id", "GytPCcUU9PC6OsOG2kLS")
            .addHeader("X-Naver-Client-Secret", "rgRNVXvLy_")
            .build()
        chain.proceed(request)
    }

    // OkHttpClient 빌드
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    // Retrofit 빌드
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환
            .build()
    }
}

