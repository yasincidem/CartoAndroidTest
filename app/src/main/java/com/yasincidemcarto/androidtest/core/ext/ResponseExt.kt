package com.yasincidemcarto.androidtest.core.ext

import com.yasincidemcarto.androidtest.api.Errors
import com.yasincidemcarto.androidtest.core.util.ResultOf
import retrofit2.Response
import java.io.IOException

suspend fun <T> processResponse(request: suspend () -> Response<T>): ResultOf<T> {
    return try {
        val response = request()
        val responseCode = response.code()
        val responseMessage = response.message()
        if (response.isSuccessful) {
            ResultOf.success(response.body()!!)
        } else {
            ResultOf.failure(Errors.NetworkError(responseCode, responseMessage))
        }
    } catch (e: IOException) {
        ResultOf.failure(Errors.NetworkError(desc = e.message))
    }
}