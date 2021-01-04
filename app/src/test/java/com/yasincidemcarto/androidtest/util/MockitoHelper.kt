package com.yasincidemcarto.androidtest.util

import org.mockito.Mockito

object MockitoHelper {
    inline fun <reified T> mockk(): T = Mockito.mock(T::class.java)
}