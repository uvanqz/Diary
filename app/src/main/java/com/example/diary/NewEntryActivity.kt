package com.example.diary

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.diary.ui.theme.DiaryTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class NewEntryActivity : ComponentActivity() {
    private lateinit var db: DiaryDatabase
    private lateinit var diaryEntryDao: DiaryEntryDao
    private var photoBitmap: Bitmap? = null

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
                                    photo = photoBitmap?.let { bitmapToByteArray(it) },
                                )
                            saveEntryToDatabase(entry)
                        },
                        onPickImage = {
                            getContent.launch("image/*")
                        },
                        photoBitmap = photoBitmap,
                    )
                }
            }
        }
    }

    @Composable
    fun NewEntryScreen(
        onSave: (String, String) -> Unit,
        onPickImage: () -> Unit,
        photoBitmap: Bitmap?,
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

            photoBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp),
                    contentScale = ContentScale.Crop,
                )
            }

            Button(onClick = onPickImage) {
                Text(stringResource(id = R.string.pick_image))
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
                val inputStream = contentResolver.openInputStream(it)
                photoBitmap = BitmapFactory.decodeStream(inputStream)
            }
        }

    private fun saveEntryToDatabase(entry: DiaryEntry) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                diaryEntryDao.insert(entry)
            }
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        val format = Bitmap.CompressFormat.PNG
        bitmap.compress(format, DEFAULT_COMPRESS_QUALITY, stream)
        return stream.toByteArray()
    }

    companion object {
        private const val DEFAULT_COMPRESS_QUALITY = 100
    }
}
