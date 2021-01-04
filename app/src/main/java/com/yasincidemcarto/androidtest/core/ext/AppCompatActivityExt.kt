package com.yasincidemcarto.androidtest.core.ext

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yasincidemcarto.androidtest.core.Constants.DEFAULT_ANIM_DELAY_LONG
import com.yasincidemcarto.androidtest.core.Constants.DEFAULT_SLIDE_ANIM_DUR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T: View> AppCompatActivity.expandBottomSheet(bottomSheetBehavior: BottomSheetBehavior<T>, delay: Boolean = false, delayAmount: Long = DEFAULT_ANIM_DELAY_LONG) {
    if (delay)
        lifecycleScope.launch {
            delay(delayAmount)
            _expandBottomSheet(bottomSheetBehavior)
        }
    else {
        _expandBottomSheet(bottomSheetBehavior)
    }
}

fun <T: View> AppCompatActivity.hideBottomSheet(bottomSheetBehavior: BottomSheetBehavior<T>, delay: Boolean = false, delayAmount: Long = DEFAULT_ANIM_DELAY_LONG) {
    if (delay)
        lifecycleScope.launch {
            delay(delayAmount)
            _hideBottomSheet(bottomSheetBehavior)
        }
    else {
        _hideBottomSheet(bottomSheetBehavior)
    }
}

private fun <T: View> _hideBottomSheet(bottomSheetBehavior: BottomSheetBehavior<T>) =
    bottomSheetBehavior.apply {
        isHideable = true
        isDraggable = false
        state = BottomSheetBehavior.STATE_HIDDEN
    }

private fun <T: View> _expandBottomSheet(bottomSheetBehavior: BottomSheetBehavior<T>) =
    bottomSheetBehavior.apply {
        isHideable = false
        isDraggable = false
        state = BottomSheetBehavior.STATE_EXPANDED
    }


fun applySlideAnimation(viewGroup: ViewGroup, visibility: Int) {
    val transition = Slide(Gravity.TOP).apply {
        duration = DEFAULT_SLIDE_ANIM_DUR
        addTarget(viewGroup.id)
    }
    TransitionManager.beginDelayedTransition(viewGroup, transition)
    viewGroup.visibility = visibility
}

fun Activity.isLocationPermissionGranted() =
    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED