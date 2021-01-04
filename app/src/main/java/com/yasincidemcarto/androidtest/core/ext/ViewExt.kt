package com.yasincidemcarto.androidtest.core.ext

import android.view.View
import com.yasincidemcarto.androidtest.core.util.SafeClickListener

fun View.setOnSafeClickListener(
    onSafeClick: (View) -> Unit
) {
    setOnClickListener(SafeClickListener { v ->
        onSafeClick(v)
    })
}
