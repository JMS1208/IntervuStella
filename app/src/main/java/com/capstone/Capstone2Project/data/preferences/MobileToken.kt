package com.capstone.Capstone2Project.data.preferences

data class MobileToken(
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        val EMPTY_TOKEN = MobileToken("", "")
    }

}