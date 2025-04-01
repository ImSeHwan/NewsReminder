package com.junseo.newsreminder.retrofit

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiClient {
    private const val BASE_URL = "https://openapi.naver.com/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val authInterceptor = Interceptor { chain ->
            val request: Request = chain.request().newBuilder()
                .addHeader("X-Naver-Client-Id", "GytPCcUU9PC6OsOG2kLS")
                .addHeader("X-Naver-Client-Secret", "rgRNVXvLy_")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}

