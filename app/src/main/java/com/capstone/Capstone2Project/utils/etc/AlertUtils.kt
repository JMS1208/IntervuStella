package com.capstone.Capstone2Project.utils.etc

import android.content.Context
import android.widget.Toast

class AlertUtils {
    companion object {
        fun showToast(
            context: Context,
            message: String,
            duration: Int = Toast.LENGTH_SHORT
        ) {
            Toast.makeText(context, message, duration).show()
        }

    }
}