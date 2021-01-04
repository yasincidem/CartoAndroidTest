package com.yasincidemcarto.androidtest.datasource.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PoisResponse<T>(
    @Json(name = "rows") val rows: List<T>,
    @Json(name = "total_rows") val totalRows: Long
)