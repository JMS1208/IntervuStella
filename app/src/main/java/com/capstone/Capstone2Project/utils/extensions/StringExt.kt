package com.capstone.Capstone2Project.utils.extensions

fun generateRandomText(length: Int = 100): String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
    return (0..length)
        .map { charset.random() }
        .joinToString("")
}