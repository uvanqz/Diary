package com.example.diary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val photo: ByteArray?,
    val date: Date = Date(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DiaryEntry) return false

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) {
            return false
        }
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (photo?.contentHashCode() ?: 0)
        result = 31 * result + date.hashCode()
        return result
    }
}
