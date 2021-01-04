package com.yasincidemcarto.androidtest.core.ext

fun <E> List<E>.applyAll(function: (E) -> Unit) {
    this.forEach { function.invoke(it) }
}