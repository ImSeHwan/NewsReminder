package com.junseo.newsreminder.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

object InputDialog {
    @Composable
    fun Show(
        initialValue: String,
        onConfirm: (String) -> Unit,
        onDismiss: () -> Unit
    ) {
        val inputState = remember { mutableStateOf(initialValue) }

        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = "검색키워드") },
            text = {
                TextField(
                    value = inputState.value,
                    onValueChange = { inputState.value = it },
                    placeholder = { Text("키워드를 입력하세요") }
                )
            },
            confirmButton = {
                Button(onClick = { onConfirm(inputState.value) }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("취소")
                }
            }
        )
    }
}

