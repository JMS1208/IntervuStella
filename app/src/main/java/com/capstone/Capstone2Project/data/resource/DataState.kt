package com.capstone.Capstone2Project.data.resource

sealed class DataState {
    object Loading : DataState()
    data class Error(val e: Throwable?, val message: String? = null) : DataState()
    object Normal : DataState()
}