package com.yellow.widgetdemo.utils

import android.content.Context

/**
 * Created by Freeman on 2020/10/27
 */
object ScreenUtils {

    @JvmStatic
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    @JvmStatic
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (0.5f + pxValue / scale).toInt()
    }

    @JvmStatic
    fun sp2px(context: Context, spValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5F)
    }

}