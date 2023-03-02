package com.capstone.Capstone2Project.data.model

import java.time.format.TextStyle
import java.util.*
import java.time.DayOfWeek

data class Attendance(
    val date: Long? = null,
    var isPresent: Boolean = false
) {
    fun timeToDayOfWeek(): String? {
        date?: return null

        val calendar = Calendar.getInstance(Locale.getDefault())

        calendar.time = Date(date)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        return DayOfWeek.of(dayOfWeek).getDisplayName(TextStyle.SHORT, Locale.US)
    }

}

data class AttendanceInfo(
    val recentConsDays: Int,
    val weekAttendance: List<Boolean>//Sun 부터 Today까지
)
