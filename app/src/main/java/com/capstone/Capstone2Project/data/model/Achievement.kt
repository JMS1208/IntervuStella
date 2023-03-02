package com.capstone.Capstone2Project.data.model

import java.text.SimpleDateFormat
import java.util.*

data class Achievement(
    val date: Long,
    val text: String
) {
    fun timeToString(): String {
        val date = Date(date)

        return SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(date)
    }
}
