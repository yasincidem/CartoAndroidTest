package com.yasincidemcarto.androidtest.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "poiTable")
data class Poi(
    @Json(name = "id")
    @PrimaryKey
    val id: String,

    @Json(name = "title")
    @ColumnInfo(name = "title")
    val title: String,

    @Json(name = "description")
    @ColumnInfo(name = "description")
    val description: String,

    @Json(name = "direction")
    @ColumnInfo(name = "direction")
    val direction: String,

    @Json(name = "region")
    @ColumnInfo(name = "region")
    val region: String,

    @Json(name = "image")
    @ColumnInfo(name = "image")
    val image: String,

    @Json(name = "longitude")
    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @Json(name = "latitude")
    @ColumnInfo(name = "latitude")
    val latitude: Double
) {
    fun getImageFixed(): String = image.replace("http:", "https:")
    fun getAsCoordinate(): LatLng = LatLng(latitude, longitude)
}