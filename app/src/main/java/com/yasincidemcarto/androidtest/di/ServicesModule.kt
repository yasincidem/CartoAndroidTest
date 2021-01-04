package com.yasincidemcarto.androidtest.di

import com.yasincidemcarto.androidtest.api.PoiService
import com.yasincidemcarto.androidtest.api.ServiceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ServicesModule {

    @Provides
    @Singleton
    fun providePoiService(retrofit: Retrofit): PoiService {
        return retrofit.create(PoiService::class.java)
    }

    @Provides
    @Singleton
    fun provideServiceManager(
        poiService: PoiService
    ): ServiceManager {
        return ServiceManager(
            poiService
        )
    }
}