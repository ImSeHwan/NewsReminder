package com.junseo.newsreminder.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.junseo.newsreminder.JSApplication
import com.junseo.newsreminder.common.CommonInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChipsItemViewModel @Inject constructor() : ViewModel() {
    var chipInfoList by mutableStateOf<List<Pair<String, Boolean>>>(emptyList())

    /// SharedPreferences에 저장된 데이터를 읽어와 chipInfoList에 설정
    fun loadChipInfoFromPreferences() {
        val savedData = JSApplication.INSTANCE.simplePrefs.getList(CommonInfo.PREF_SEARCH_LIST_KEY)

        chipInfoList = savedData.mapNotNull { item ->
            val parts = item.split(",")
            if (parts.size == 2) {
                val label = parts[0]
                val isSelected = parts[1].toBooleanStrictOrNull() ?: false
                Pair(label, isSelected)
            } else {
                null // 형식이 이상하면 무시
            }
        }
    }
}