package com.yasincidemcarto.androidtest.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.yasincidemcarto.androidtest.core.Constants.QUERY
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
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var repository: PoiRepository
    private lateinit var viewModel: MainViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repository = mock(PoiRepository::class.java)
        viewModel = MainViewModel(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun loadListOfPoi() = runBlockingTest {
        viewModel.listOfPoi.observeForever(mockk())
        verify(repository).getPois(QUERY)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun searchQuery()  {
        val query = "sydney"
        viewModel.setSearchQuery(query)
        viewModel.searchQuery.observeForever(mockk())
        assertThat(viewModel.searchQuery.value).matches(query)
    }

    @Test
    fun checkSelectedAndPreviousPoints() {
        viewModel.selectedPoint.observeForever(mockk())
        viewModel.updatePoint(mockk())
        assertThat(viewModel.previousPoint.value).isNull()
        assertThat(viewModel.selectedPoint.value).isNotNull()
        viewModel.updatePoint(mockk())
        assertThat(viewModel.previousPoint.value).isNotNull()
        assertThat(viewModel.selectedPoint.value).isNotNull()
    }

    @Test
    fun checkLoadingState() = runBlockingTest {
        assertThat(viewModel.isLoading.value).isTrue()
        viewModel.listOfPoi.observeForever(mockk())
        verify(repository).getPois(QUERY)
        assertThat(viewModel.isLoading.value).isFalse()
    }

    @Test
    fun checkCalculateRoute() {
        val distance = viewModel.calculateDistance(mockk(), mockk())
        assertThat(distance).isNotNull()
    }

}