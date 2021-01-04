package com.yasincidemcarto.androidtest.core.ext

import android.location.Location
import com.google.android.gms.maps.model.LatLng

fun LatLng.toLocation(): Location {
    val location = Location("someLoc")
    location.latitude = latitude
    location.longitude = longitude
    return location
}

