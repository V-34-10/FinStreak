package com.finance.finstreak.data.model

import java.time.LocalDate

enum class DayStatus {
    SAFE,
    OVERSPEND
}

data class DayEntry(
    val id: Long = 0L,
    val date: LocalDate,
    val status: DayStatus,
    val note: String = "",
    val resilienceScore: Int? = null,
    val criteriaNote: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class StreakData(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalSafeDays: Int,
    val totalDays: Int,
    val lastEntryDate: LocalDate?
)

data class AnalyticsSummary(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalSafeDays: Int,
    val totalOverspendDays: Int,
    val averageResilienceScore: Float,
    val safeRate: Float,
    val weeklyData: List<WeeklyPoint>,
    val monthlyData: List<MonthlyPoint>
)

data class WeeklyPoint(
    val label: String,
    val safeDays: Int,
    val totalDays: Int
)

data class MonthlyPoint(
    val month: String,
    val safeDays: Int,
    val overspendDays: Int
)
