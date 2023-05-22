package com.earth.libbase.util

import android.app.Activity

object ActivityStackManager {

    private var activities = mutableListOf<Activity>()
    private var activitieShare = mutableListOf<Activity>()

    fun addActivity(activity: Activity) = activities.add(activity)
    fun addShareActivity(activity: Activity) = activitieShare.add(activity)

    fun removeActivity(activity: Activity) {
        if (activities.contains(activity)) {
            activities.remove(activity)
            activity.finish()
        }
    }

    fun getTopActivity(): Activity? =
        if (activities.isEmpty()) null else activities[activities.size - 1]

    fun finishAll() {
        for (item in activities){
            item.finish()
        }
    //    activities.filter { it.isFinishing }.forEach { it.finish() }
    }
    fun finishSahreAll() {
        for (item in activitieShare){
            item.finish()
        }
        //    activities.filter { it.isFinishing }.forEach { it.finish() }
    }
}