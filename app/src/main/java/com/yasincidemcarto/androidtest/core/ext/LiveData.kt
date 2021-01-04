package com.yasincidemcarto.androidtest.core.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <A, B> zipFirstNullableLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A?, B>> {
    return MediatorLiveData<Pair<A?, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            val localLastA: A? = lastA
            val localLastB = lastB
            if (localLastB != null)
                this.value = Pair(localLastA, localLastB)
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
    }
}

fun <A, B> zipNonNullableLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            if (localLastA != null && localLastB != null)
                this.value = Pair(localLastA, localLastB)
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
    }
}
