package com.junseo.newsreminder.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junseo.newsreminder.JSApplication
import com.junseo.newsreminder.common.CommonInfo
import com.junseo.newsreminder.model.NewsItem
import com.junseo.newsreminder.retrofit.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor( private val repository: Repository) : ViewModel() {
    // 뉴스 아이템
    val newsItems = mutableStateListOf<NewsItem>()
    // 칩 정보
    val chipInfoList = mutableStateListOf<Pair<String, Boolean>>()
    // 다이얼로그 관리
    val showDialog = mutableStateOf(false)
    val inputText = mutableStateOf("")

    fun fetchData(query: String) {
        viewModelScope.launch {
            try {
                val result = repository.patchNewsList(query)

                result.onSuccess { newsList ->
                    // 이미지 URL 추출 후 데이터를 업데이트
                    val updatedItems = newsList.items.map { item ->
                        val imageUrl = fetchImageUrlFromLink(item.link) ?: ""
                        item.copy(imageUrl = imageUrl)  // 이미지 URL을 새로운 필드에 추가
                    }

                    newsItems.clear()
                    newsItems.addAll(updatedItems)

                }.onFailure { error ->
                    Log.e("NewsItemViewModel", "error : $error")
                }

            } catch (e: Exception) {
                //_data.value = "API 호출 실패"
                Log.e("NewsItemViewModel", "e : $e")
            }
        }
    }

    fun loadSavedChipInfo(savedData: List<String>) {
        chipInfoList.clear()
        chipInfoList.addAll(savedData.map { it.split(",") }.map { it[0] to it[1].toBoolean() })
    }

    fun loadChipInfoFromPreferences() {
        val savedData = JSApplication.INSTANCE.simplePrefs.getList(CommonInfo.PREF_SEARCH_LIST_KEY)
        loadSavedChipInfo(savedData)
    }

    fun addChip(chip: String) {
        chipInfoList.add(chip to true)
        fetchData(chip)
    }

    fun removeChip(chip: String) {
        chipInfoList.removeIf { it.first == chip }
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