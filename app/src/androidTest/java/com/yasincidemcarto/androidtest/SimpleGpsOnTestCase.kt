package com.yasincidemcarto.androidtest

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.yasincidemcarto.androidtest.core.util.EspressoIdlingResource
import com.yasincidemcarto.androidtest.ui.activity.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SimpleGpsOnTestCase {

    private val firstPoiTitle = "Sydney Harbour Bridge"

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity>
            = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unRegisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun userFlowWithGpsOn() {
        clickSearchBarAndCheckIfBottomSheetFragmentShowsUp()
        checkIfFirstElementOfTheListNotNull()
        clickTheFirstElement()
        clickDirectionButtonAndCheckRouteBottomSheet()
        clickStartButtonAndCheckNavigationBottomSheet()
    }

    private fun clickSearchBarAndCheckIfBottomSheetFragmentShowsUp() {
        onView(withId(R.id.searchPoiView)).perform(click())
        onView(withId(R.id.materialCardView)).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    private fun checkIfFirstElementOfTheListNotNull() {
        onView(withId(R.id.listOfPoiRecyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(matches(hasDescendant(withText(firstPoiTitle))))
    }

    private fun clickTheFirstElement() {
        onView(withId(R.id.listOfPoiRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.poi_title)).check(matches(withText(firstPoiTitle)))
    }

    private fun clickDirectionButtonAndCheckRouteBottomSheet() {
        onView(withId(R.id.directions))
            .perform(click())
        onView(withId(R.id.poi_start_title)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    private fun clickStartButtonAndCheckNavigationBottomSheet() {
        onView(withId(R.id.navigate))
            .perform(click())
        onView(withId(R.id.navigation_info)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}