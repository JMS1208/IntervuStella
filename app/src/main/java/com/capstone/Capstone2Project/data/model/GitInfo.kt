package com.capstone.Capstone2Project.data.model

import com.google.gson.annotations.SerializedName

data class GitLanguage(
    @SerializedName("lang")
    val name: String,
    @SerializedName("percent")
    val ratio: Float
)