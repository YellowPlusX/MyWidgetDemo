package com.yellow.widgetdemo.utils

import android.util.Log

/**
 * Created by Freeman on 2020/10/27
 */
object LogUtil {

    private const val TAG = "LogUtil"

    @JvmStatic
    fun i(tag: String, log: String) {
        Log.i(TAG, "$tag: $log")
    }

    @JvmStatic
    fun e(tag: String, log: String) {
        Log.e(TAG, "$tag: $log")
    }
}