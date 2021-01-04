package com.yasincidemcarto.androidtest.datasource.remote

import com.yasincidemcarto.androidtest.api.ServiceManager
import com.yasincidemcarto.androidtest.core.base.repository.IRemoteDataSource
import com.yasincidemcarto.androidtest.core.ext.processResponse
import com.yasincidemcarto.androidtest.core.util.ResultOf
import com.yasincidemcarto.androidtest.datasource.model.Poi
import com.yasincidemcarto.androidtest.datasource.model.PoisResponse
import javax.inject.Inject

class PoiRemoteDataSource @Inject constructor(
    private val serviceManager: ServiceManager
) : IRemoteDataSource {

    suspend fun fetch(query: String): ResultOf<PoisResponse<Poi>> {
        return processResponse { serviceManager.poiService.getPois(query) }
    }

}