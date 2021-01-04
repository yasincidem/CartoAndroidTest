package com.yasincidemcarto.androidtest.repository

import androidx.lifecycle.LiveData
import com.yasincidemcarto.androidtest.datasource.model.Poi

interface PoiRepositoryContractor {
    suspend fun getPois(query: String) : LiveData<List<Poi>>
    suspend fun getFilteredPois(searchQuery: String) : LiveData<List<Poi>>
}