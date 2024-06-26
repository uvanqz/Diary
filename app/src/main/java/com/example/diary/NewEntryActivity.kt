package com.example.diary

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diary.ui.theme.DiaryTheme

class NewEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiaryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    NewEntryScreen(onSave = { title, description ->
                        val resultIntent =
                            Intent().apply {
                                putExtra("TITLE", title)
                                putExtra("DESCRIPTION", description)
                            }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    })
                }
            }
        }
    }
}

@Composable
fun NewEntryScreen(onSave: (String, String) -> Unit) {
    val titleState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = titleState.value,
            onValueChange = { titleState.value = it },
            label = { Text("Заголовок") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = descriptionState.value,
            onValueChange = { descriptionState.value = it },
            label = { Text("Описание") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onSave(titleState.value, descriptionState.value) }) {
            Text("Сохранить")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewEntryScreenPreview() {
    DiaryTheme {
        NewEntryScreen { _, _ -> }
    }
}
