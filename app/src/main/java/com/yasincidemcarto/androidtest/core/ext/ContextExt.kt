package com.yasincidemcarto.androidtest.core.ext

import android.content.Context
import android.util.TypedValue

fun Context.toPixelFromDip(value: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics).toInt()
