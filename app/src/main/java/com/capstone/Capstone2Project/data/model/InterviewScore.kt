package com.capstone.Capstone2Project.data.model

import com.google.gson.annotations.SerializedName
import com.madrapps.plot.line.DataPoint

data class InterviewScore(
    @SerializedName("max_score")
    val maxRank: String,
    @SerializedName("min_score")
    val minRank: String,
    @SerializedName("recently_date")
    val recentlyDate: Long,
    @SerializedName("scores")
    val ranks: List<Rank>
) {
    companion object {
        fun createTestObject(): InterviewScore {

            val ranks = mutableListOf<Rank>()

            for(i in 0 until 5) {
                ranks.add(
                    Rank.makeTestRank()
                )
            }

            return InterviewScore(
                maxRank = "S",
                minRank = "C",
                recentlyDate = System.currentTimeMillis(),
                ranks = ranks
            )
        }
    }
}

data class Rank(
    @SerializedName("date")
    val date: Long,
    @SerializedName("score")
    val rank: String
) {
    companion object {
        fun makeTestRank(): Rank {

            val currentTime = System.currentTimeMillis()

            val ranks = arrayOf("A","B","C","D","S")

            return Rank(
                date = (currentTime-1000000..currentTime).random(),
                rank = ranks.random()
            )
        }
    }

    fun rankToDataPointY(): Float {
        return when(rank) {
            "A"-> 30f
            "B"-> 20f
            "C"-> 10f
            "D"-> 0f
            "S"-> 40f
            else -> 40f
        }
    }

}
