package com.yasincidemcarto.androidtest.repository.impl

import androidx.lifecycle.LiveData
import com.yasincidemcarto.androidtest.core.base.repository.BaseRepositoryBoth
import com.yasincidemcarto.androidtest.core.util.ResultOf
import com.yasincidemcarto.androidtest.datasource.local.PoiLocalDataSource
import com.yasincidemcarto.androidtest.datasource.model.Poi
import com.yasincidemcarto.androidtest.datasource.model.PoisResponse
import com.yasincidemcarto.androidtest.datasource.remote.PoiRemoteDataSource
import com.yasincidemcarto.androidtest.repository.PoiRepositoryContractor
import javax.inject.Inject

class PoiRepository @Inject constructor(
    remoteDataSource: PoiRemoteDataSource,
    localDataSource: PoiLocalDataSource
) : BaseRepositoryBoth<PoiRemoteDataSource, PoiLocalDataSource>(remoteDataSource, localDataSource), PoiRepositoryContractor {

    private suspend fun getListOfPoiFromRemote(query: String): ResultOf<PoisResponse<Poi>> {
        return try {
            when(val response = remoteDataSource.fetch(query)) {
                is ResultOf.Success -> {
                    localDataSource.savePois(response.data.rows)
                    ResultOf.Success(response.data)
                }
                is ResultOf.Failure -> {
                    ResultOf.failure(error = Throwable("Unknown Error"))
                }
            }
        } catch (err: Exception) {
            ResultOf.failure(error = err)
        }
    }

    override suspend fun getPois(query: String): LiveData<List<Poi>> {
        return localDataSource.getPois().also {
            getListOfPoiFromRemote(query)
        }
    }

    override suspend fun getFilteredPois(searchQuery: String): LiveData<List<Poi>> {
        return localDataSource.getFilteredPois(searchQuery)
    }

}