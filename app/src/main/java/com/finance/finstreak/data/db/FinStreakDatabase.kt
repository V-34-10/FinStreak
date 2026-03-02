package com.finance.finstreak.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finance.finstreak.data.db.dao.DayEntryDao
import com.finance.finstreak.data.db.entity.DayEntryEntity

@Database(
    entities = [DayEntryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FinStreakDatabase : RoomDatabase() {
    abstract fun dayEntryDao(): DayEntryDao

    companion object {
        const val DATABASE_NAME = "finstreak_db"
    }
}
