package com.junseo.newsreminder.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.junseo.newsreminder.JSApplication
import com.junseo.newsreminder.common.CommonInfo
import com.junseo.newsreminder.retrofit.Repository
import com.msinfotech.delivery.utils.prefs.SimplePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChipsItemViewModel @Inject constructor() : ViewModel() {
    var chipInfoList by mutableStateOf<List<Pair<String, Boolean>>?>(null)

    /// SharedPreferences에 저장된 데이터를 읽어와 chipInfoList에 설정
    fun loadChipInfoFromPreferences(savedData: List<String>) {
        chipInfoList = savedData.map {
            Pair(it.split(",")[0], it.split(",")[1].toBoolean())
        }
    }
}