package com.capstone.Capstone2Project.data.model.fornetwork

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("ques_uuid")
    val questionUUID: String,
    @SerializedName("user_uuid")
    val hostUUID: String,
    @SerializedName("comment")
    val comment: String
)