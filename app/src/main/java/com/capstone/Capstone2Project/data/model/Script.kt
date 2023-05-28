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
    var jobRole: String? = null
): Parcelable {
    fun toJsonString(): String {

        val gsonBuilder = GsonBuilder().setLenient()

        val gson = gsonBuilder.create()

        val result = gson.toJson(this)

        Log.e("TAG", "toJsonString 변환결과: $result", )

        return result

//        val moshi = Moshi.Builder()
//            .addLast(KotlinJsonAdapterFactory())
//            .build()
//
//        val jsonAdapter: JsonAdapter<Script> = moshi.adapter(Script::class.java).lenient()
//
//        return jsonAdapter.toJson(this)
    }

    companion object {

        fun jsonStringToScript(jsonString: String): Script? {
            Log.e("TAG", "jsonStringToScript: $jsonString", )

            return try {
//                val moshi = Moshi.Builder()
//                    .addLast(KotlinJsonAdapterFactory())
//                    .build()
//
//                val jsonAdapter: JsonAdapter<Script> = moshi.adapter(Script::class.java).lenient()
//
//                jsonAdapter.fromJson(jsonString)
                val gson = Gson()

                gson.fromJson<Script>(jsonString, Script::class.java)

            } catch (e:Exception) {
                e.printStackTrace()
                null
            }

        }

//        fun makeTestScript(): Script {
//
//            val items = listOf(
//                ScriptItem.createTestScriptItem(),
//                ScriptItem.createTestScriptItem(),
//                ScriptItem.createTestScriptItem(),
//                ScriptItem.createTestScriptItem(),
//            )
//
//            return Script(
//                date = System.currentTimeMillis(),
//                title = "자기소개서",
//                interviewed = false,
//                scriptItems = items,
//                jobRole = "안드로이드 개발자"
//            )
//        }
    }


}

/*

 */
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

