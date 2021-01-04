package com.yasincidemcarto.androidtest.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        activityScope.launch {
            delay(1000)
            finishSplashActivity()
        }
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }

    override fun onRestart() {
        super.onRestart()
        finishSplashActivity()
    }

    private fun finishSplashActivity() {
        val option = ActivityOptionsCompat.makeCustomAnimation(this,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
        startActivity(Intent(
                this@SplashActivity,
                MainActivity::class.java), option
        )

        finish()
    }
}