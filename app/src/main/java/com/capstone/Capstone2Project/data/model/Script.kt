package com.capstone.Capstone2Project.data.model

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

data class Script(
    val uuid: String = UUID.randomUUID().toString(),
    val host: String,
    var date: Long,
    var name: String,
    val questionnaireState: Boolean,
    val scriptItems: List<ScriptItem>
) {
    fun toJsonString(): String {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val jsonAdapter: JsonAdapter<Script> = moshi.adapter(Script::class.java)

        return jsonAdapter.toJson(this)
    }

    companion object {

        fun jsonStringToScript(jsonString: String): Script? {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val jsonAdapter: JsonAdapter<Script> = moshi.adapter(Script::class.java)

            return jsonAdapter.fromJson(jsonString)
        }

        fun makeTestScript(): Script {
            val tips = listOf("팁 예시1111111111", "팁 예시22222222222", "팁 예시3333333333")

            val scriptUUID = UUID.randomUUID().toString()

            val items = listOf(
                ScriptItem(question = "예시 질문", tips = tips, maxLength = 300, scriptUUID = scriptUUID),
                ScriptItem(question = "예시 질문", tips = tips, maxLength = 500, scriptUUID = scriptUUID),
                ScriptItem(question = "예시 질문", tips = tips, maxLength = 300, scriptUUID = scriptUUID),
                ScriptItem(question = "예시 질문", tips = tips, maxLength = 500, scriptUUID = scriptUUID),
            )

            return Script(
                host = UUID.randomUUID().toString(),
                date = System.currentTimeMillis(),
                name = "자기소개서",
                questionnaireState = false,
                scriptItems = items
            )
        }
    }


}

data class ScriptItem(
    val itemUUID: String = UUID.randomUUID().toString(),
    val scriptUUID: String,
    val question: String,
    var answer: String = "",
    val maxLength: Int,
    val tips: List<String>
)
