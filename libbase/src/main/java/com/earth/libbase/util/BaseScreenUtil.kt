package com.earth.libbase.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationManagerCompat
import com.earth.libbase.base.BaseApplication


object BaseScreenUtil {

    fun getScreenWidth(): Int {
        val windowManager = BaseApplication.instance
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        return size.x
    }
    // 获取标题栏高度
    fun getActivityMessageHeight(): Int {
        var resources=BaseApplication.instance.resources
        var hight=resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(hight)
    }
    fun getScreenHeight(): Int {
        val windowManager = BaseApplication.instance
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        return size.y
    }
    fun checkNotifySetting(context: Context?): Boolean {
        val manager = NotificationManagerCompat.from(context!!)
        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
        return manager.areNotificationsEnabled()
    }

    fun getString(resId: Int): String? {
        val res: Resources = BaseApplication.instance.resources
        return res.getString(resId)
    }
     fun setAndroidNativeLightStatusBar(activity: Activity, dark: Boolean) {
        val decor = activity.window.decorView
        if (dark) {
            decor.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            decor.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }
    fun getPixelsFromDp(i: Int,context: Activity?): Int {
        val metrics = DisplayMetrics()
        context?.windowManager?.defaultDisplay?.getMetrics(metrics)
        return i * metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
    }

}