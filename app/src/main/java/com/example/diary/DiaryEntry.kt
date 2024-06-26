package com.example.diary

import java.util.*

data class DiaryEntry(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val date: Date = Date(),
    val photoUri: String? = null,
)
