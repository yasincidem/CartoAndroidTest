package com.yasincidemcarto.androidtest.datasource.local

import androidx.lifecycle.LiveData
import com.yasincidemcarto.androidtest.core.base.repository.ILocalDataSource
import com.yasincidemcarto.androidtest.datasource.local.room.PoiDao
import com.yasincidemcarto.androidtest.datasource.model.Poi
import javax.inject.Inject

class PoiLocalDataSource @Inject constructor(
    private val poiDao: PoiDao
): ILocalDataSource {

    fun getPois(): LiveData<List<Poi>> {
        return poiDao.getPois()
    }

    fun getFilteredPois(searchQuery: String) : LiveData<List<Poi>> {
        return poiDao.getFilteredPois(searchQuery)
    }

    suspend fun savePois(pois: List<Poi>) {
        poiDao.savePois(pois)
    }

}