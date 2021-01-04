package com.yasincidemcarto.androidtest.api

import com.yasincidemcarto.androidtest.datasource.model.Poi
import com.yasincidemcarto.androidtest.datasource.model.PoisResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PoiService {
    @GET("sql")
    suspend fun getPois(@Query("q") query: String): Response<PoisResponse<Poi>>
}