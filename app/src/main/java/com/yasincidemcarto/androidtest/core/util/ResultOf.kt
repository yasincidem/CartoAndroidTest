package com.yasincidemcarto.androidtest.core.util

sealed class ResultOf<out T> {

    companion object {
        fun <T> success(result: T): ResultOf<T> = Success(result)
        fun <T> failure(error: Throwable): ResultOf<T> = Failure(error)
    }

    data class Failure(val error: Throwable) : ResultOf<Nothing>()
    data class Success<out T>(val data: T) : ResultOf<T>()
}