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

    private val imageCache = mutableMapOf<String, String>()

    fun fetchData(keyword: String) {
        viewModelScope.launch {
            try {
                val result = repository.patchNewsList(keyword)

                result.onSuccess { newsList ->
                    // 1. 먼저 빈 imageUrl로 UI에 보여주기
                    _data.value = newsList.copy(
                        items = newsList.items.map { it.copy(imageUrl = "") }
                    )

                    // 2. 각 아이템에 대해 비동기적으로 이미지 요청
                    newsList.items.forEachIndexed { index, item ->
                        launch(Dispatchers.IO) {
                            val imageUrl = fetchImageUrlFromLink(item.link) ?: ""

                            // 3. 기존 데이터에서 해당 아이템만 업데이트
                            val currentItems = _data.value?.items?.toMutableList() ?: return@launch
                            currentItems[index] = currentItems[index].copy(imageUrl = imageUrl)
                            _data.value = _data.value?.copy(items = currentItems)
                        }
                    }
                }.onFailure { error ->
                    Log.e("NewsItemViewModel", "error : $error")
                }

            } catch (e: Exception) {
                Log.e("NewsItemViewModel", "e : $e")
            }
        }
    }

    private suspend fun fetchImageUrlFromLink(link: String): String? {
        imageCache[link]?.let { return it }

        return try {
            val document = withContext(Dispatchers.IO) {
                Jsoup.connect(link)
                    .timeout(3000)
                    .userAgent("Mozilla")
                    .get()
            }
            val url = document.select("meta[property=og:image]").attr("content")
            imageCache[link] = url
            url
        } catch (e: Exception) {
            Log.e("ImageFetch", "Failed to fetch image", e)
            null
        }
    }
}