package com.yasincidemcarto.androidtest.core.ext

import android.content.res.Resources
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

private const val AVG_SPEED_KMH = 80.0f
private const val KM = 1000.0f
private const val HOUR_TO_MINUTES = 60.0f

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Float.metersToKm(): String = this.toDouble().metersToKm()

private fun Double.metersToKm(): String =
    if (this < KM)
        formatDecimals().format(this) + "m"
    else
        formatDecimals().format(this / KM) + "km"


fun Float.time(): String =
    if (this / KM < AVG_SPEED_KMH)
        formatDecimals().format(((this / KM) / AVG_SPEED_KMH) * HOUR_TO_MINUTES) + "min"
    else
        formatDecimals().format((this / KM) / AVG_SPEED_KMH) + "h"

private fun formatDecimals(): NumberFormat {
    val locale = Locale.getDefault()
    val result = NumberFormat.getInstance(locale)
    result.minimumFractionDigits = 2
    result.maximumFractionDigits = 2

    if (result is DecimalFormat) result.isDecimalSeparatorAlwaysShown = false

    return result
}