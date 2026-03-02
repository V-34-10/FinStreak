package com.finance.finstreak.data.repository

import com.finance.finstreak.data.db.dao.DayEntryDao
import com.finance.finstreak.data.db.entity.DayEntryEntity
import com.finance.finstreak.data.model.AnalyticsSummary
import com.finance.finstreak.data.model.DayEntry
import com.finance.finstreak.data.model.DayStatus
import com.finance.finstreak.data.model.MonthlyPoint
import com.finance.finstreak.data.model.StreakData
import com.finance.finstreak.data.model.WeeklyPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DayEntryRepository(private val dao: DayEntryDao) {

    fun getAllEntries(): Flow<List<DayEntry>> =
        dao.getAllEntries().map { list -> list.map { it.toDomain() } }

    fun searchEntries(query: String): Flow<List<DayEntry>> =
        dao.searchEntries(query).map { list -> list.map { it.toDomain() } }

    fun getEntriesInRange(from: LocalDate, to: LocalDate): Flow<List<DayEntry>> =
        dao.getEntriesInRange(from.toString(), to.toString())
            .map { list -> list.map { it.toDomain() } }

    suspend fun getEntryById(id: Long): DayEntry? =
        dao.getEntryById(id)?.toDomain()

    suspend fun getEntryByDate(date: LocalDate): DayEntry? =
        dao.getEntryByDate(date.toString())?.toDomain()

    suspend fun saveEntry(entry: DayEntry): Long =
        dao.insertEntry(DayEntryEntity.fromDomain(entry))

    suspend fun updateEntry(entry: DayEntry) =
        dao.updateEntry(DayEntryEntity.fromDomain(entry))

    suspend fun deleteEntry(entry: DayEntry) =
        dao.deleteEntry(DayEntryEntity.fromDomain(entry))

    suspend fun deleteAllEntries() = dao.deleteAllEntries()

    suspend fun getStreakData(): StreakData {
        val allEntries = dao.getRecentEntries(1000)
        val sorted = allEntries.sortedByDescending { it.date }
        val domainEntries = sorted.map { it.toDomain() }

        val currentStreak = calculateCurrentStreak(domainEntries)
        val longestStreak = calculateLongestStreak(domainEntries)
        val totalSafe = dao.getTotalSafeDays()
        val total = dao.getTotalDays()
        val latest = dao.getLatestEntry()?.toDomain()

        return StreakData(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalSafeDays = totalSafe,
            totalDays = total,
            lastEntryDate = latest?.date
        )
    }

    suspend fun getAnalyticsSummary(periodDays: Int = 30): AnalyticsSummary {
        val from = LocalDate.now().minusDays(periodDays.toLong())
        val allEntries = dao.getRecentEntries(1000).map { it.toDomain() }.sortedByDescending { it.date }
        val periodEntries = allEntries.filter { !it.date.isBefore(from) }

        val totalSafe = periodEntries.count { it.status == DayStatus.SAFE }
        val totalOverspend = periodEntries.count { it.status == DayStatus.OVERSPEND }
        val avgResilience = dao.getAverageResilienceScore() ?: 0f
        val safeRate = if (periodEntries.isNotEmpty()) totalSafe.toFloat() / periodEntries.size else 0f

        val currentStreak = calculateCurrentStreak(allEntries)
        val longestStreak = calculateLongestStreak(allEntries)

        val weekly = buildWeeklyData(allEntries)
        val monthly = buildMonthlyData(allEntries)

        return AnalyticsSummary(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalSafeDays = totalSafe,
            totalOverspendDays = totalOverspend,
            averageResilienceScore = avgResilience,
            safeRate = safeRate,
            weeklyData = weekly,
            monthlyData = monthly
        )
    }

    private fun calculateCurrentStreak(entries: List<DayEntry>): Int {
        if (entries.isEmpty()) return 0
        val sorted = entries.sortedByDescending { it.date }
        var streak = 0
        var expectedDate = LocalDate.now()

        for (entry in sorted) {
            if (entry.date == expectedDate || entry.date == expectedDate.minusDays(1)) {
                if (entry.status == DayStatus.SAFE) {
                    streak++
                    expectedDate = entry.date.minusDays(1)
                } else {
                    break
                }
            } else if (entry.date.isBefore(expectedDate)) {
                break
            }
        }
        return streak
    }

    private fun calculateLongestStreak(entries: List<DayEntry>): Int {
        if (entries.isEmpty()) return 0
        val sorted = entries.sortedBy { it.date }
        var longest = 0
        var current = 0

        for (entry in sorted) {
            if (entry.status == DayStatus.SAFE) {
                current++
                if (current > longest) longest = current
            } else {
                current = 0
            }
        }
        return longest
    }

    private fun buildWeeklyData(entries: List<DayEntry>): List<WeeklyPoint> {
        val weeks = mutableListOf<WeeklyPoint>()
        val now = LocalDate.now()
        for (i in 3 downTo 0) {
            val weekStart = now.minusWeeks(i.toLong()).with(java.time.DayOfWeek.MONDAY)
            val weekEnd = weekStart.plusDays(6)
            val weekEntries = entries.filter { !it.date.isBefore(weekStart) && !it.date.isAfter(weekEnd) }
            val safeDays = weekEntries.count { it.status == DayStatus.SAFE }
            val formatter = DateTimeFormatter.ofPattern("dd MMM")
            weeks.add(WeeklyPoint(weekStart.format(formatter), safeDays, weekEntries.size))
        }
        return weeks
    }

    private fun buildMonthlyData(entries: List<DayEntry>): List<MonthlyPoint> {
        val months = mutableListOf<MonthlyPoint>()
        val now = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MMM")
        for (i in 5 downTo 0) {
            val month = now.minusMonths(i.toLong())
            val monthEntries = entries.filter {
                it.date.month == month.month && it.date.year == month.year
            }
            months.add(
                MonthlyPoint(
                    month = month.format(formatter),
                    safeDays = monthEntries.count { it.status == DayStatus.SAFE },
                    overspendDays = monthEntries.count { it.status == DayStatus.OVERSPEND }
                )
            )
        }
        return months
    }
}
