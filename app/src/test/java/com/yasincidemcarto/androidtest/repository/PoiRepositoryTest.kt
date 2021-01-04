package com.yasincidemcarto.androidtest.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.yasincidemcarto.androidtest.core.util.ResultOf
import com.yasincidemcarto.androidtest.datasource.local.PoiLocalDataSource
import com.yasincidemcarto.androidtest.datasource.remote.PoiRemoteDataSource
import com.yasincidemcarto.androidtest.repository.impl.PoiRepository
import com.yasincidemcarto.androidtest.util.MockitoHelper.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class PoiRepositoryTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var repository: PoiRepository
    private lateinit var poiRemoteDataSource: PoiRemoteDataSource
    private lateinit var poiLocalDataSource: PoiLocalDataSource
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        poiRemoteDataSource = mock(PoiRemoteDataSource::class.java)
        poiLocalDataSource = mock(PoiLocalDataSource::class.java)
        repository = PoiRepository(poiRemoteDataSource, poiLocalDataSource)
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun checkBothRemoteAndLocalReturnNull() = runBlockingTest {
        `when`(poiLocalDataSource.getPois()).thenReturn(null)
        `when`(poiRemoteDataSource.fetch("foo")).thenReturn(null)
        val data = repository.getPois("foo")
        assertThat(data).isNull()
    }

    @Test
    fun checkOnlyRemoteReturnNull() = runBlockingTest {
        `when`(poiLocalDataSource.getPois()).thenReturn(mockk())
        `when`(poiRemoteDataSource.fetch("foo")).thenReturn(null)
        val data = repository.getPois("foo")
        assertThat(data).isNotNull()
    }

    @Test
    fun checkFilteredPoiList() = runBlockingTest {
        `when`(poiLocalDataSource.getFilteredPois("sydney")).thenReturn(mockk())
        val data = repository.getFilteredPois("sydney")
        assertThat(data).isNotNull()
    }

}