package com.finance.finstreak.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finance.finstreak.data.db.entity.DayEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DayEntryDao {

    @Query("SELECT * FROM day_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<DayEntryEntity>>

    @Query("SELECT * FROM day_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): DayEntryEntity?

    @Query("SELECT * FROM day_entries WHERE date = :date LIMIT 1")
    suspend fun getEntryByDate(date: String): DayEntryEntity?

    @Query("SELECT * FROM day_entries ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentEntries(limit: Int): List<DayEntryEntity>

    @Query("SELECT * FROM day_entries WHERE note LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchEntries(query: String): Flow<List<DayEntryEntity>>

    @Query("SELECT * FROM day_entries WHERE date >= :from AND date <= :to ORDER BY date DESC")
    fun getEntriesInRange(from: String, to: String): Flow<List<DayEntryEntity>>

    @Query("SELECT COUNT(*) FROM day_entries WHERE status = 'SAFE'")
    suspend fun getTotalSafeDays(): Int

    @Query("SELECT COUNT(*) FROM day_entries")
    suspend fun getTotalDays(): Int

    @Query("SELECT * FROM day_entries ORDER BY date DESC LIMIT 1")
    suspend fun getLatestEntry(): DayEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DayEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: DayEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: DayEntryEntity)

    @Query("DELETE FROM day_entries")
    suspend fun deleteAllEntries()

    @Query("SELECT AVG(resilience_score) FROM day_entries WHERE resilience_score IS NOT NULL")
    suspend fun getAverageResilienceScore(): Float?
}
