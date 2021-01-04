package com.yasincidemcarto.androidtest.datasource.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yasincidemcarto.androidtest.datasource.model.Poi

@Database(entities = [Poi::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun poiDao(): PoiDao

}