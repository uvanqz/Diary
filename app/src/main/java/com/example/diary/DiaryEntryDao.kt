package com.example.diary

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DiaryEntryDao {
    @Insert
    suspend fun insert(entry: DiaryEntry)

    @Query("select * from diary_entries")
    suspend fun getAllEntries(): List<DiaryEntry>
}
