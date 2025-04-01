package com.junseo.newsreminder.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junseo.newsreminder.model.NaverNewsResponse
import com.junseo.newsreminder.retrofit.ApiClient
import com.junseo.newsreminder.retrofit.ApiService
import com.junseo.newsreminder.retrofit.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class NewsItemViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _data = MutableStateFlow<NaverNewsResponse?>(null)
    val data: StateFlow<NaverNewsResponse?> = _data

    fun fetchData(keyword: String) {
        viewModelScope.launch {
            try {
                //val apiService = ApiClient.retrofit.create(ApiService::class.java)
                //val repository = Repository(apiService)
                val result = repository.patchNewsList(keyword)

                result.onSuccess { newsList ->
                    // 이미지 URL 추출 후 데이터를 업데이트
                    val updatedItems = newsList.items.map { item ->
                        val imageUrl = fetchImageUrlFromLink(item.link) ?: ""
                        item.copy(imageUrl = imageUrl)  // 이미지 URL을 새로운 필드에 추가
                    }

                    _data.value = newsList.copy(items = updatedItems)
                }.onFailure { error ->
                    Log.e("NewsItemViewModel", "error : $error")
                }

            } catch (e: Exception) {
                //_data.value = "API 호출 실패"
                Log.e("NewsItemViewModel", "e : $e")
            }
        }
    }

    private suspend fun fetchImageUrlFromLink(link: String): String? {
        return try {
            val document = withContext(Dispatchers.IO) {
                Jsoup.connect(link).get()
            }
            document.select("meta[property=og:image]").attr("content") // og:image 메타 태그
        } catch (e: Exception) {
            Log.e("ImageFetch", "Failed to fetch image", e)
            null
        }
    }
}