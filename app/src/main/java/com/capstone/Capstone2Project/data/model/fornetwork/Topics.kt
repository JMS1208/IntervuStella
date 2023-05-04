package com.capstone.Capstone2Project.data.model.fornetwork

import com.capstone.Capstone2Project.data.model.Topic
import com.google.gson.annotations.SerializedName

data class Topics(
    @SerializedName("interesting_field")
    val topics: List<String> = emptyList()
) {
    companion object {
        fun createTestTopics(): List<Topic> {
            return listOf(
                Topic(name = "운영체제", selected = true),
                Topic(name = "네트워크", selected = true),
                Topic(name = "데이터베이스", selected = true),
                Topic(name = "알고리즘", selected = true),
                Topic(name = "자료구조", selected = true),
                Topic(name = "프로그래밍 기초", selected = true),
                Topic(name = "JAVA", selected = true),
                Topic(name = "전산 기본", selected = true)
            )
        }
    }
}
