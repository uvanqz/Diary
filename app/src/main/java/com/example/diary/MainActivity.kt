package com.example.diary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.diary.ui.theme.DiaryTheme
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val diaryEntries = mutableStateListOf<DiaryEntry>()

    private val newEntryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val title = result.data?.getStringExtra("TITLE").orEmpty()
                val description = result.data?.getStringExtra("DESCRIPTION").orEmpty()
                val photoUri = result.data?.getStringExtra("PHOTO_URI")

                if (title.isNotEmpty() && description.isNotEmpty() && photoUri != null) {
                    diaryEntries.add(DiaryEntry(title = title, description = description, photoUri = Uri.parse(photoUri)))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            DiaryTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    DiaryApp(
                        diaryEntries,
                        isDarkTheme,
                        onNewEntryClick = { navigateToNewEntry(isDarkTheme) },
                        onThemeToggleClick = { isDarkTheme = !isDarkTheme },
                    )
                }
            }
        }
    }

    private fun navigateToNewEntry(isDarkTheme: Boolean) {
        val intent = Intent(this, NewEntryActivity::class.java)
        intent.putExtra("IS_DARK_THEME", isDarkTheme)
        newEntryLauncher.launch(intent)
    }
}

@Composable
fun DiaryApp(
    entries: List<DiaryEntry>,
    isDarkTheme: Boolean,
    onNewEntryClick: () -> Unit,
    onThemeToggleClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(onClick = onNewEntryClick) {
                Text(stringResource(id = R.string.create_new_entry))
            }
            Button(onClick = onThemeToggleClick) {
                Text(if (isDarkTheme) stringResource(id = R.string.light_theme) else stringResource(id = R.string.dark_theme))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        DiaryList(entries)
    }
}

@Composable
fun DiaryList(entries: List<DiaryEntry>) {
    LazyColumn {
        items(entries) { entry ->
            DiaryEntryItem(entry)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DiaryEntryItem(entry: DiaryEntry) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = entry.title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = entry.description, style = MaterialTheme.typography.bodyMedium)
        entry.photoUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberImagePainter(uri),
                contentDescription = "Photo",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                contentScale = ContentScale.Crop,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
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
        DiaryApp(
            entries = emptyList(),
            isDarkTheme = false,
            onNewEntryClick = {},
            onThemeToggleClick = {},
        )
    }
}
