package com.example.diary

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import coil.compose.rememberImagePainter
import com.example.diary.ui.theme.DiaryTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewEntryActivity : ComponentActivity() {
    private lateinit var db: DiaryDatabase
    private lateinit var diaryEntryDao: DiaryEntryDao
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db =
            Room.databaseBuilder(
                applicationContext,
                DiaryDatabase::class.java,
                "diary_database",
            ).build()
        diaryEntryDao = db.diaryEntryDao()

        setContent {
            val isDarkTheme = intent.getBooleanExtra("IS_DARK_THEME", false)

            DiaryTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    NewEntryScreen(
                        onSave = { title, description ->
                            val entry =
                                DiaryEntry(
                                    title = title,
                                    description = description,
                                    photoUri = photoUri, // Сохраняем URI как Uri
                                )
                            saveEntryToDatabase(entry)
                        },
                        onPickImage = {
                            // Запуск активити выбора изображения
                            getContent.launch("image/*")
                        },
                        photoUri = photoUri,
                    )
                }
            }
        }
    }

    @Composable
    fun NewEntryScreen(
        onSave: (String, String) -> Unit,
        onPickImage: () -> Unit,
        photoUri: Uri?,
    ) {
        var titleState by remember { mutableStateOf("") }
        var descriptionState by remember { mutableStateOf("") }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                value = titleState,
                onValueChange = { titleState = it },
                label = { Text(stringResource(id = R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = descriptionState,
                onValueChange = { descriptionState = it },
                label = { Text(stringResource(id = R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onPickImage) {
                Text(stringResource(id = R.string.pick_image))
            }

            photoUri?.let { uri ->
                Image(
                    painter = rememberImagePainter(uri),
                    contentDescription = "Image from $uri",
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onSave(titleState, descriptionState) }) {
                Text(stringResource(id = R.string.save))
            }
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                photoUri = it
            }
        }

    private fun saveEntryToDatabase(entry: DiaryEntry) {
        // Сохранение записи в базу данных Room асинхронно
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                diaryEntryDao.insert(entry)
            }
            setResult(RESULT_OK)
            finish()
        }
    }
}
