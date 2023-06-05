package com.capstone.Capstone2Project.data.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*
data class Topic(
//    val uuid: String = UUID.randomUUID().toString(),
    @SerializedName("field_name")
    val name: String,
    @SerializedName("selected")
    var selected: Boolean
)