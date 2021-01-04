package com.yasincidemcarto.androidtest.core

import com.google.android.gms.maps.model.LatLng
import com.yasincidemcarto.androidtest.datasource.model.Poi

object Constants {
    const val DATABASE_NAME = "poi_database"
    const val BASE_URL = "https://javieraragon.carto.com/api/v2/"
    const val DEFAULT_SLIDE_ANIM_DUR = 500L
    const val DEFAULT_ANIM_DELAY_LONG = 100L
    const val DEFAULT_ANIM_DELAY_SLOW = 10L
    const val FAKE_LOCATION_TITLE = "Your Location"
    val FAKE_LOCATION = LatLng(-33.3000802, 149.0913524)
    val FAKE_POI = Poi(
        id = "fake_poi",
        title = FAKE_LOCATION_TITLE,
        description = "",
        direction = "",
        region = "",
        image = "",
        longitude = 149.0913524,
        latitude = -33.3000802
    )
    const val QUERY = "SELECT id, direction, href as image, region, title, view as description, " +
                "ST_X(the_geom) as longitude, ST_Y(the_geom) as latitude FROM ios_test"
}