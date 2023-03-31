package com.capstone.Capstone2Project.data.model

import com.google.gson.annotations.SerializedName

data class Topics(
    @SerializedName("interesting_list")
    val topics: List<String> = emptyList()
)
