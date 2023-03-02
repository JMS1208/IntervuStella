package com.capstone.Capstone2Project.data.model.camera

import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*

data class InterviewVideo(
    val id: Long,
    val displayName: String,
    val contentUri: Uri,
    val dateTaken: Long
) {
    companion object {
        fun dateToTimeStamp(date: Long): String {
            return SimpleDateFormat(
                TIMESTAMP_FORMAT,
                Locale.getDefault()
            ).format(date)
        }

        private const val TIMESTAMP_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
