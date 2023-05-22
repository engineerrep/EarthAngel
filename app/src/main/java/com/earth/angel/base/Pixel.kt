package com.earth.angel.base

import android.content.Context

/**
 * create by zhaimi
 * 像素转换工具
 */

/**
 * int to Px
 * val width = 15.dp2Px()
 * val width = 15.dp2Px(context)
 */
fun Int.dp2Px(context: Context? = null): Int {
    val ctx = context ?: EarthAngelApp.instance
    return (this * ctx.resources.displayMetrics.density + 0.5f).toInt()
}

/**
 * float to Px
 * val width = 1f.dp2Px()
 * val width = 1f.dp2Px(context)
 */
fun Float.dp2Px(context: Context? = null): Int {
    val ctx = context ?: EarthAngelApp.instance
    return (this * ctx.resources.displayMetrics.density + 0.5f).toInt()
}

/**
 * int sp to Px
 * val width = 15.sp2Px()
 * val width = 15.sp2Px(context)
 */
fun Int.sp2Px(context: Context? = null): Int {
    val ctx = context ?: EarthAngelApp.instance
    return (this * ctx.resources.displayMetrics.scaledDensity + 0.5f).toInt()
}

/**
 * float sp to Px
 * val width = 1f.sp2Px()
 * val width = 1f.sp2Px(context)
 */
fun Float.sp2Px(context: Context? = null): Int {
    val ctx = context ?: EarthAngelApp.instance
    return (this * ctx.resources.displayMetrics.scaledDensity + 0.5f).toInt()
}