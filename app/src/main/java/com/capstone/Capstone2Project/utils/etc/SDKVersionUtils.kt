package com.capstone.Capstone2Project.utils.etc

import android.os.Build

inline fun <T> sdk29AndUp(onSdk29: ()->T): T? {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29()
    } else null
}

inline fun <T> sdk31AndUp(onSdk31: ()->T): T? {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        onSdk31()
    } else null
}