package com.capstone.Capstone2Project.data.model

import android.os.Parcelable
import android.util.Log
import com.capstone.Capstone2Project.utils.extensions.generateRandomText
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.util.*

@kotlinx.parcelize.Parcelize
data class Script(
    @SerializedName("script_uuid")
    val uuid: String = UUID.randomUUID().toString(), //내가 만들기
    @SerializedName("host_uuid")
    val hostUUID: String,
    @SerializedName("date")
    var date: Long? = null,//
    @SerializedName("script_title")
    var title: String = "",//
    @SerializedName("interviewed")
    val interviewed: Boolean = false,
    @SerializedName("script_items")
    var scriptItems: List<ScriptItem> = emptyList(),
    @SerializedName("role")
    var jobRole: String
): Parcelable {
    fun toJsonString(): String {

        val gsonBuilder = GsonBuilder().setLenient()

        val gson = gsonBuilder.create()

        return gson.toJson(this)

    }

    companion object {
        fun createTestingScript(): Script {

            val scriptItems = mutableListOf<ScriptItem>()

            for(i in 0 until 4) {
                scriptItems.add(
                    ScriptItem.createTestScriptItem()
                )
            }

            return Script(
                hostUUID = UUID.randomUUID().toString(),
                title = "안드로이드 개발자로서의 꿈",
                interviewed = true,
                jobRole = "안드로이드",
                scriptItems = scriptItems,
                date = System.currentTimeMillis()
            )
        }
        fun jsonStringToScript(jsonString: String): Script? {

            return try {

                val gson = Gson()

                gson.fromJson<Script>(jsonString, Script::class.java)

            } catch (e:Exception) {
                e.printStackTrace()
                null
            }

        }

    }


}

@kotlinx.parcelize.Parcelize
data class ScriptItem(
    @SerializedName("script_item_uuid") //question 에 대한 uuid
    val itemUUID: String = UUID.randomUUID().toString(),
    @SerializedName("script_item_question")
    val question: String,
    @SerializedName("script_item_answer")
    var answer: String = "",
    @SerializedName("script_item_answer_max_length")
    val maxLength: Int,
    @SerializedName("tips")
    val tips: List<String>,
    @SerializedName("index")
    val index: Int
): Parcelable {
    companion object {
        fun createTestScriptItem(): ScriptItem {
            val tips = listOf("A","B","C","D")

            return ScriptItem(
                maxLength = 500,
                tips = tips,
                index = (0..5).random(),
                question = "테스트 질문 ${(0..5).random()}",
                answer = generateRandomText(100)
            )
        }
    }
}

