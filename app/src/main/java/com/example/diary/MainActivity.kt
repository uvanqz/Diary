package com.example.diary

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diary.ui.theme.DiaryTheme
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val diaryEntries = mutableStateListOf<DiaryEntry>()

    private val newEntryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val title = result.data?.getStringExtra("TITLE").orEmpty()
                val description = result.data?.getStringExtra("DESCRIPTION").orEmpty()
                if (title.isNotEmpty() && description.isNotEmpty()) {
                    diaryEntries.add(DiaryEntry(title = title, description = description))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiaryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    DiaryApp(diaryEntries) { navigateToNewEntry() }
                }
            }
        }
    }

    private fun navigateToNewEntry() {
        val intent = Intent(this, NewEntryActivity::class.java)
        newEntryLauncher.launch(intent)
    }
}

@Composable
fun DiaryApp(
    entries: List<DiaryEntry>,
    onNewEntryClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Button(onClick = onNewEntryClick) {
            Text("Создать новую запись")
        }
        Spacer(modifier = Modifier.height(16.dp))
        DiaryList(entries)
    }
}

@Composable
fun DiaryList(entries: List<DiaryEntry>) {
    Column {
        entries.forEach { entry ->
            DiaryEntryItem(entry)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DiaryEntryItem(entry: DiaryEntry) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = entry.title, style = MaterialTheme.typography.titleMedium)
        Text(text = entry.description, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(entry.date),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryAppPreview() {
    DiaryTheme {
        DiaryApp(emptyList()) {}
    }
}
