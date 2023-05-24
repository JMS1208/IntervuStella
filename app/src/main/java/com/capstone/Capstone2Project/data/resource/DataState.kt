package com.capstone.Capstone2Project.data.resource

sealed class DataState {
    data class Loading(
        val message: String? = null
    ) : DataState()
    data class Error(val e: Throwable?, val message: String? = null) : DataState()
    object Normal : DataState()
}