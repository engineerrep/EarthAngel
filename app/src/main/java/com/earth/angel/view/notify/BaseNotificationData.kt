package com.earth.angel.view.notify

open interface BaseNotificationData {

    fun getSmallIcon(): Int

    fun getContentTitle(): CharSequence?

    fun getContentText(): CharSequence?

}