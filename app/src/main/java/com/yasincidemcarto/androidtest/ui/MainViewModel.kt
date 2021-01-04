package com.yasincidemcarto.androidtest.ui

import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.yasincidemcarto.androidtest.core.Constants.QUERY
import com.yasincidemcarto.androidtest.core.util.EspressoIdlingResource
import com.yasincidemcarto.androidtest.core.util.ResultOf
import com.yasincidemcarto.androidtest.datasource.model.Poi
import com.yasincidemcarto.androidtest.repository.impl.PoiRepository

class MainViewModel @ViewModelInject constructor(
    private val repository: PoiRepository
) : ViewModel() {

    val listOfPoi: LiveData<ResultOf<List<Poi>>> by lazy { getInitialPois() }
    val selectedPoint: MutableLiveData<Poi> = MutableLiveData(null)
    val previousPoint: MutableLiveData<Poi> = MutableLiveData(null)
    val filteredListOfPoi: LiveData<ResultOf<List<Poi>>> by lazy {
        Transformations.switchMap(searchQuery) {
            getFilteredPois(it)
        }
    }
    var isLoading = MutableLiveData(true)
    val searchQuery = MutableLiveData("")

    private val _startPoint: MutableLiveData<Poi?> = MutableLiveData(null)
    val startPoint: LiveData<Poi?> = _startPoint

    private val _endPoint: MutableLiveData<Poi?> = MutableLiveData(null)
    val endPoint: LiveData<Poi?> = _endPoint

    private val _isFakeLocationEnabled = MutableLiveData(false)
    val isFakeLocationEnabled = _isFakeLocationEnabled

    fun getInitialPois(query: String = QUERY): LiveData<ResultOf<List<Poi>>> {
        return liveData {
            EspressoIdlingResource.increment()

            isLoading.value = true
            try {
                emitSource(repository.getPois(query).map { ResultOf.Success(data = it) })
            } catch (err: Exception) {
                emit(ResultOf.Failure(error = err))
            }
            isLoading.value = false
            EspressoIdlingResource.decrement()
        }
    }

    private fun getFilteredPois(searchQuery: String?): LiveData<ResultOf<List<Poi>>> {
        return liveData {
            EspressoIdlingResource.increment()
            isLoading.value = true
            try {
                if (searchQuery == null || searchQuery == "")
                    emitSource(listOfPoi)
                else
                    emitSource(repository.getFilteredPois(searchQuery).map { ResultOf.Success(data = it) })
            } catch (err: Exception) {
                emit(ResultOf.Failure(error = err))
            }
            isLoading.value = false
            EspressoIdlingResource.decrement()
        }
    }

    fun setSearchQuery(searchedText: String? = null) {
        searchQuery.value = searchedText
    }

    fun updatePoint(newPoi: Poi) {
        previousPoint.value = selectedPoint.value
        selectedPoint.value = newPoi
    }

    fun setStartPoint(start: Poi?) {
        _startPoint.value = start
    }

    fun setEndPoint(end: Poi?) {
        _endPoint.value = end
    }

    fun setIsFakeLocationEnabled(isEnabled: Boolean): Boolean {
        _isFakeLocationEnabled.value = isEnabled
        return isEnabled
    }

    fun calculateDistance(start: LatLng, end: LatLng): Float {
        val result = floatArrayOf(0f)
        Location.distanceBetween(
            start.latitude,
            start.longitude,
            end.latitude,
            end.longitude,
            result
        )
        return result.first()
    }
}