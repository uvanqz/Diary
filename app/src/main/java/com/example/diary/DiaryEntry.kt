package com.example.diary

import android.net.Uri
import java.util.*

data class DiaryEntry(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val date: Date = Date(),
    val photoUri: Uri?, // Используем Uri для хранения ссылки на фото,
)
