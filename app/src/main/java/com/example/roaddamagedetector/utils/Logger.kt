package com.example.roaddamagedetector.utils

import android.util.Log

object Logger {

    private const val TAG = "Classifier"

    @JvmStatic
    fun v(log: String) {
        Log.v(TAG, log)
    }

    @JvmStatic
    fun d(log: String) {
        Log.d(TAG, log)
    }

    @JvmStatic
    fun e(log: String) {
        Log.e(TAG, log)
    }
}