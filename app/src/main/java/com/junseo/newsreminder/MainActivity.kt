package com.junseo.newsreminder

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.junseo.newsreminder.common.CommonInfo
import com.junseo.newsreminder.dialog.InputDialog
import com.junseo.newsreminder.model.NaverNewsResponse
import com.junseo.newsreminder.model.NewsItem
import com.junseo.newsreminder.ui.theme.NewsReminderTheme
import com.junseo.newsreminder.utils.log.MLog
import com.junseo.newsreminder.viewmodel.ChipsItemViewModel
import com.junseo.newsreminder.viewmodel.NewsItemViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val chipsItemViewModel: ChipsItemViewModel by viewModels()
    private val newsItemViewModel: NewsItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            NewsReminderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainUI(
                        chipsViewModel = chipsItemViewModel,
                        newsItemViewModel = newsItemViewModel
                    )
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
fun MainUI(
    chipsViewModel: ChipsItemViewModel,
    newsItemViewModel: NewsItemViewModel
) {

    // 데이터 로딩 (최초 한 번만)
    LaunchedEffect(Unit) {
        // SharedPreferences 데이터 불러오기
        chipsViewModel.loadChipInfoFromPreferences()
    }

    val newsData by newsItemViewModel.data.collectAsState()

    val showDialog = rememberSaveable { mutableStateOf(false) }
    val inputText = rememberSaveable { mutableStateOf("") }

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
            ChipGroup(onRemove = { chip ->
                chipsViewModel.chipInfoList = chipsViewModel.chipInfoList
                    .filter { it.first != chip } // 같은 String 값을 가진 항목 제거
                    .toMutableList()
                    .apply {
                        if (none { it.second } && isNotEmpty()) {
                            // 리스트에 true가 없고, 비어있지 않다면 마지막 아이템을 true로 변경
                            this[lastIndex] = this[lastIndex].first to true
                        }
                    }
            })

            Spacer(modifier = Modifier.height(16.dp))

            // 리스트뷰
            ItemList(newsResponse = newsData)
        }
    }

    // 다이얼로그 표시
    if (showDialog.value) {
        InputDialog(
            initialValue = inputText.value,
            onConfirm = { newValue ->
                if(newValue.isBlank()) {
                    Toast.makeText(context, "키워드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    chipsViewModel.chipInfoList = chipsViewModel.chipInfoList.map { it.first to false }.toMutableList().apply {
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
fun ChipGroup(
    chipsViewModel: ChipsItemViewModel = hiltViewModel(),
    newsItemViewModel: NewsItemViewModel = hiltViewModel(),
              onRemove: (String) -> Unit) {
    var selectedChip by remember { mutableStateOf<String?>(null) }

    //var selectedChip by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(chipsViewModel.chipInfoList) {
        selectedChip = chipsViewModel.chipInfoList.firstOrNull { it.second }?.first
        Log.d("sehwan", "selectedChip : $selectedChip")

        selectedChip?.let {
            newsItemViewModel.fetchData(it)
        }

    }
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        maxItemsInEachRow = 3, // 한 줄에 최대 3개까지 배치
        horizontalArrangement = Arrangement.spacedBy(8.dp), // 칩 간 간격
        verticalArrangement = Arrangement.spacedBy(8.dp) // 줄 간 간격
    ) {
        chipsViewModel.chipInfoList.forEach { (chip, selected) ->
            AssistChip(
                onClick = {
                    if (!selected) { // 이미 선택된 경우 무시
                        chipsViewModel.chipInfoList = chipsViewModel.chipInfoList.map {
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
fun ItemList(newsResponse: NaverNewsResponse?) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        newsResponse?.items?.let { items ->
            items(items, key = { it.title }) { newsItem ->
                NewsItemView(currentNewsItem = newsItem)
            }
        }
    }
}

@Composable
fun NewsItemView(currentNewsItem: NewsItem) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { openWebPage(context, currentNewsItem.link) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
        containerColor = Color.White
                )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AsyncImage(
                model = currentNewsItem.imageUrl,
                contentDescription = "뉴스 이미지",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                HtmlText(currentNewsItem.title, maxLines = 1)  // HTML 제목
                Spacer(modifier = Modifier.height(8.dp))
                HtmlText(currentNewsItem.description, maxLines = 3) // 최대 3줄로 제한

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentNewsItem.pubDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun HtmlText(html: String, maxLines: Int = Int.MAX_VALUE) {
    AndroidView(factory = { context ->
        TextView(context).apply {
            text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            setMaxLines(maxLines) // 최대 줄 수 제한
            ellipsize = android.text.TextUtils.TruncateAt.END // 생략 부호 처리
        }
    })
}

// ✅ Intent 실행 함수 - Composable 함수 외부로 분리
fun openWebPage(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    //val mockViewModel = NewsItemViewModel() // ✅ 직접 ViewModel 인스턴스 생성

    NewsReminderTheme {
        //MainUI()
    }
}