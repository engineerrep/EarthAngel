package com.earth.angel.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationManagerCompat
import com.earth.angel.base.EarthAngelApp


object ScreenUtil {

    fun getScreenWidth(): Int {
        val windowManager = EarthAngelApp.instance
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        return size.x
    }
    fun getActivityMessageHeight(context: Context?): Int {
        var statusBarHeight2 = -1
      /*  try {
            val clazz = Class.forName("com.android.internal.R\$dimen")
            val `object` = clazz.newInstance()
            val height = clazz.getField("status_bar_height")[`object`].toString().toInt()
            statusBarHeight2 = context!!.resources.getDimensionPixelSize(height)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return statusBarHeight2*/
        var resources=EarthAngelApp.instance.resources
        var hight=resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(hight)
    }
    fun getScreenHeight(): Int {
        val windowManager = EarthAngelApp.instance
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

    fun getHeight(): Int {
        val windowManager = EarthAngelApp.instance
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay.height
        return display
    }
    fun getString(resId: Int): String? {
        val res: Resources = EarthAngelApp.instance.resources
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
}