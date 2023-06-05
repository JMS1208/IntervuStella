package com.capstone.Capstone2Project.utils.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.toFormatString(format: String): String {
    val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())

    return simpleDateFormat.format(this)
}

fun Long.toFormatString(format: String): String {
    val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())

    return simpleDateFormat.format(Date(this))
}