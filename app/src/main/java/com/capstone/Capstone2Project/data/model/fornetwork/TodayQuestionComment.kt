package com.capstone.Capstone2Project.data.model.fornetwork


import com.google.gson.annotations.SerializedName

/*
조회용
 */
data class TodayQuestionComment(
    @SerializedName("cc_uuid")
    val commentUUID: String,
    @SerializedName("comment")
    var comment: String,
    @SerializedName("common_ques")
    val question: String,
    @SerializedName("date")
    val date: Long,
    @SerializedName("recommendation")
    var like: Int,
    @SerializedName("user_uuid")
    val commentWriterUUID: String,
    @SerializedName("viewer_liked")
    var isLiked: Boolean,
    @SerializedName("name")
    val nickName: String,
    @SerializedName("email")
    val email: String
)