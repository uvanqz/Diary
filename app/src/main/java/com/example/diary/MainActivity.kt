package com.example.diary

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.diary.ui.theme.DiaryTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val diaryEntries = mutableStateListOf<DiaryEntry>()
    private lateinit var db: DiaryDatabase
    private lateinit var diaryEntryDao: DiaryEntryDao

    private val newEntryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadEntries()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db =
            Room.databaseBuilder(
                applicationContext,
                DiaryDatabase::class.java,
                "diary_database",
            )
                .fallbackToDestructiveMigration()
                .build()
        diaryEntryDao = db.diaryEntryDao()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            DiaryTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    DiaryApp(
                        entries = diaryEntries,
                        isDarkTheme = isDarkTheme,
                        onNewEntryClick = { navigateToNewEntry(isDarkTheme) },
                        onThemeToggleClick = { isDarkTheme = !isDarkTheme },
                    )
                }
            }
        }

        loadEntries()
    }

    private fun loadEntries() {
        lifecycleScope.launch {
            val entries = withContext(Dispatchers.IO) { diaryEntryDao.getAllEntries() }
            diaryEntries.clear()
            diaryEntries.addAll(entries)
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
                Text(stringResource(R.string.create_new_entry))
            }
            Button(onClick = onThemeToggleClick) {
                Text(if (isDarkTheme) stringResource(R.string.light_theme) else stringResource(R.string.dark_theme))
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

        entry.photo?.let { photo ->
            val bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.size)
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
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
