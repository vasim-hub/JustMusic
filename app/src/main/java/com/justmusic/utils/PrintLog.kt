package com.justmusic.utils

import android.util.Log
import com.justmusic.BuildConfig

object PrintLog {
    fun printLogD(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }
}
