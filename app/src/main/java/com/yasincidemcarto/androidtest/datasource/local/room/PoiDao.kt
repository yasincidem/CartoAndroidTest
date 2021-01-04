package com.yasincidemcarto.androidtest.datasource.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.yasincidemcarto.androidtest.datasource.model.Poi

@Dao
interface PoiDao {

    @Query("SELECT * FROM poiTable")
    fun getPois(): LiveData<List<Poi>>

    @Query("SELECT * FROM poiTable WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%' ")
    fun getFilteredPois(searchQuery: String): LiveData<List<Poi>>

    @Insert(onConflict = REPLACE)
    suspend fun savePois(pois: List<Poi>)

}