package com.junseo.newsreminder

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.junseo.newsreminder.common.CommonInfo
import com.junseo.newsreminder.dialog.InputDialog
import com.junseo.newsreminder.ui.theme.NewsReminderTheme
import com.junseo.newsreminder.viewmodel.ChipsItemViewModel
import com.junseo.newsreminder.viewmodel.NewsItemViewModel
import com.msinfotech.delivery.utils.prefs.SimplePrefs


class MainActivity : ComponentActivity() {
    //private val newsItemViewModel: NewsItemViewModel by viewModels()
    private val chipsItemViewModel: ChipsItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedData = JSApplication.INSTANCE.simplePrefs.getList(CommonInfo.PREF_SEARCH_LIST_KEY)
        chipsItemViewModel.loadChipInfoFromPreferences(savedData)

        setContent {
            //val viewModel: NewsItemViewModel = viewModel()

            NewsReminderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainUI(chipsItemViewModel)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        JSApplication.INSTANCE.simplePrefs.setList(
            CommonInfo.PREF_SEARCH_LIST_KEY,
            chipsItemViewModel.chipInfoList?.map { "${it.first},${it.second}" } ?: emptyList()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUI(chipsViewModel: ChipsItemViewModel, newsItemViewModel: NewsItemViewModel = NewsItemViewModel()) {
    // mutableStateListOf로 상태를 관리하여 리스트가 변경될 때 UI가 갱신되도록 수정
    //val newsData by viewModel.data.collectAsState()
    //var newValue by remember { mutableStateOf<String?>(null) }


    //val chipList = remember { mutableStateListOf<String>() }
    val itemList = remember { mutableStateListOf<String>() }

    val showDialog = remember { mutableStateOf(false) }
    val inputText = remember { mutableStateOf("") }

    val context = JSApplication.INSTANCE.applicationContext

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("뉴스 리마인더") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog.value = true // 다이얼로그 표시
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "탭 추가")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Chip 그룹
            ChipGroup(viewModel = chipsViewModel, onRemove = { chip ->
                chipsViewModel.chipInfoList = chipsViewModel.chipInfoList
                    ?.filter { it.first != chip } // 같은 String 값을 가진 항목 제거
                    ?.toMutableList()
                    ?.apply {
                        if (none { it.second } && isNotEmpty()) {
                            // 리스트에 true가 없고, 비어있지 않다면 마지막 아이템을 true로 변경
                            this[lastIndex] = this[lastIndex].first to true
                        }
                    }
            })
            //ChipGroup(viewModel = chips, onRemove = )

            Spacer(modifier = Modifier.height(16.dp))

            // 리스트뷰
            ItemList(itemList)
        }
    }

    // 다이얼로그 표시
    if (showDialog.value) {
        InputDialog.Show(
            initialValue = inputText.value,
            onConfirm = { newValue ->
                if(newValue.isBlank()) {
                    Toast.makeText(context, "키워드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    chipsViewModel.chipInfoList = chipsViewModel.chipInfoList?.map { it.first to false }?.toMutableList()?.apply {
                        add(Pair(newValue, true))
                    }
                    showDialog.value = false // 다이얼로그 닫기
                    newsItemViewModel.fetchData(newValue)
                }
            },
            onDismiss = {
                showDialog.value = false // 다이얼로그 닫기
            }
        )
    }
}

// Chip 그룹
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipGroup(viewModel: ChipsItemViewModel, onRemove: (String) -> Unit) {
    var selectedChip by remember { mutableStateOf<String?>(null) }

    //var selectedChip by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(viewModel.chipInfoList) {
        selectedChip = viewModel.chipInfoList?.firstOrNull { it.second }?.first
        Log.d("sehwan", "selectedChip : $selectedChip")
    }
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        maxItemsInEachRow = 3, // 한 줄에 최대 3개까지 배치
        horizontalArrangement = Arrangement.spacedBy(8.dp), // 칩 간 간격
        verticalArrangement = Arrangement.spacedBy(8.dp) // 줄 간 간격
    ) {
        viewModel.chipInfoList?.forEach { (chip, selected) ->
            AssistChip(
                onClick = {
                    if (!selected) { // 이미 선택된 경우 무시
                        viewModel.chipInfoList = viewModel.chipInfoList?.map {
                            it.first to (it.first == chip) // 선택한 항목만 true, 나머지는 false
                        }
                    }
                },
                modifier = Modifier.height(32.dp),
                label = { Text(chip) },
                trailingIcon = {
                    IconButton(onClick = { onRemove(chip) }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "삭제")
                    }
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selected) Color.White else Color.LightGray
                )
            )
        }
    }
}

// 리스트뷰
@Composable
fun ItemList(items: List<String>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items) { item ->
            ListItem(text = item)
        }
    }
}

// 리스트 아이템
@Composable
fun ListItem(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    val mockViewModel = NewsItemViewModel() // ✅ 직접 ViewModel 인스턴스 생성

    NewsReminderTheme {
        //MainUI(viewModel = mockViewModel)
    }
}