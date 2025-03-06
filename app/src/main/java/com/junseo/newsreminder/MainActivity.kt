package com.junseo.newsreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.junseo.newsreminder.ui.theme.NewsReminderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsReminderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainUI()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    NewsReminderTheme {
        MainUI()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUI() {
    // mutableStateListOf로 상태를 관리하여 리스트가 변경될 때 UI가 갱신되도록 수정
    //val chipList = remember { mutableStateListOf("Chip 1", "ChipChip 2", "ChipChipChip 3") }
    val chipList = remember { mutableStateListOf<String>() }
    val itemList by remember { mutableStateOf((1..10).map { "Item $it" }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("뉴스 리마인더") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                chipList.add("Chip ${chipList.size + 1}")
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "탭 추가")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Chip 그룹
            ChipGroup(chipList) { chip ->
                chipList.remove(chip)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 리스트뷰
            ItemList(itemList)
        }
    }
}

// Chip 그룹
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipGroup(chipList: List<String>, onRemove: (String) -> Unit) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        maxItemsInEachRow = 3, // 한 줄에 최대 3개까지 배치
        horizontalArrangement = Arrangement.spacedBy(8.dp), // 칩 간 간격
        verticalArrangement = Arrangement.spacedBy(8.dp) // 줄 간 간격
    ) {
        chipList.forEach { chip ->
            AssistChip(
                onClick = { /* 클릭 이벤트 */ },
                modifier = Modifier.height(32.dp),
                label = { Text(chip) },
                trailingIcon = {
                    IconButton(onClick = { onRemove(chip) }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "삭제")
                    }
                }
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
