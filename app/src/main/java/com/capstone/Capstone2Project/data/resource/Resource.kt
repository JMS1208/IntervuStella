package com.capstone.Capstone2Project.data.resource

sealed class Resource<out T> {
    object Loading: Resource<Nothing>()
    data class Success<T>(val data: T): Resource<T>()
    data class Error<T>(val error: Throwable?): Resource<T>()
}

fun <T> Resource<T>.successOrNull(): T? = if (this is Resource.Success<T>) {
    data
} else {
    null
}

fun <T> Resource<T>.throwableOrNull(): Throwable? = if (this is Resource.Error<T>) {
    error
} else {
    null
}
