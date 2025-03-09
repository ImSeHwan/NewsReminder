package com.junseo.newsreminder.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junseo.newsreminder.model.NaverNewsResponse
import com.junseo.newsreminder.retrofit.ApiClient
import com.junseo.newsreminder.retrofit.ApiService
import com.junseo.newsreminder.retrofit.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsItemViewModel: ViewModel() {
    private val _data = MutableStateFlow<NaverNewsResponse?>(null)
    val data: StateFlow<NaverNewsResponse?> = _data

    fun fetchData(keyword: String) {
        viewModelScope.launch {
            try {
                val apiService = ApiClient.retrofit.create(ApiService::class.java)
                val repository = Repository(apiService)
                val result = repository.patchNewsList(keyword)

                result.onSuccess { newsList ->
                    _data.value = newsList
                }.onFailure { error ->
                    Log.e("NewsItemViewModel", "error : $error")
                }

            } catch (e: Exception) {
                //_data.value = "API 호출 실패"
                Log.e("NewsItemViewModel", "e : $e")
            }
        }
    }
}