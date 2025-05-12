package com.junseo.newsreminder.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun InputDialog(
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by rememberSaveable { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
        title = {
            Text("키워드 입력")
        },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("키워드") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

