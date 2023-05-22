package com.earth.libbase.util

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.earth.libbase.R
import com.earth.libbase.base.BaseApplication
import com.scwang.smart.refresh.layout.util.SmartUtil
import java.util.regex.Pattern


object AppUtils {


    fun isValidEmail(email: String?): Boolean {
        if (null == email || "" == email) return false
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        val p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*") //复杂匹配
        val m = p.matcher(email)
        return m.matches()
    }

     fun isNotificationEnabled(context: Context): Boolean {
        var isOpened = false
        isOpened = try {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return isOpened
    }

     fun gotoSet(context: Context) {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
        } else {
            // 其他
            intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            intent.data = Uri.fromParts("package", context.packageName, null)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

     fun showPermissionDialog(context: Context) {
        val permissionDialog: AlertDialog = AlertDialog.Builder(context)
            .setMessage(BaseApplication.instance.getString(R.string.basepermission_content))
            .setPositiveButton(BaseApplication.instance.getString(R.string.basesetting),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                    val packageURI = Uri.parse("package:" + context.packageName)
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                    context.startActivity(intent)
                })
            .setNegativeButton(BaseApplication.instance.getString(R.string.basecancel),
                DialogInterface.OnClickListener { dialog, which -> //关闭页面或者做其他操作
                    dialog.cancel()
                })
            .create()
        permissionDialog.show()
    }

    fun isLocationServiceEnable(context: Context): Boolean{
        var lm=context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun foToOpenGps(context: Context){
        var intent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }


     fun isTopActivity(activityName: String): Boolean {
        val manager =BaseApplication.instance.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfos = manager.getRunningTasks(1)
        var cmpNameTemp: String? = null
        if (runningTaskInfos != null) {
            cmpNameTemp = runningTaskInfos[0].topActivity.toString()
        }
        return if (cmpNameTemp == null) {
            false
        } else cmpNameTemp == activityName
    }
    fun dp2px(dpValue: Float): Int {
        return (0.5f + dpValue * density).toInt()
    }

    //</editor-fold>
    private val density = Resources.getSystem().displayMetrics.density

}