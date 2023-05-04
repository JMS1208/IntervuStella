package com.capstone.Capstone2Project.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserInfo(
    @field:Json(name="name")
    val name: String,
    @field:Json(name="email")
    val email: String
)
