package com.capstone.Capstone2Project.utils.etc

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.getSystemService

fun invokeVibration(context: Context) {
    val vib = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
        vib.vibrate(
            VibrationEffect.createOneShot(
                100,
                100
            )
        )
    } else {
        @Suppress("DEPRECATION")
        vib.vibrate(100)
    }

}