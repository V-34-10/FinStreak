package com.finance.finstreak.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finance.finstreak.data.model.DayEntry
import com.finance.finstreak.data.model.DayStatus
import java.time.LocalDate

@Entity(tableName = "day_entries")
data class DayEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "note") val note: String = "",
    @ColumnInfo(name = "resilience_score") val resilienceScore: Int? = null,
    @ColumnInfo(name = "criteria_note") val criteriaNote: String = "",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): DayEntry = DayEntry(
        id = id,
        date = LocalDate.parse(date),
        status = DayStatus.valueOf(status),
        note = note,
        resilienceScore = resilienceScore,
        criteriaNote = criteriaNote,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(entry: DayEntry): DayEntryEntity = DayEntryEntity(
            id = entry.id,
            date = entry.date.toString(),
            status = entry.status.name,
            note = entry.note,
            resilienceScore = entry.resilienceScore,
            criteriaNote = entry.criteriaNote,
            createdAt = entry.createdAt
        )
    }
}
