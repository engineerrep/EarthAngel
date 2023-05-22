package com.earth.libbase.util

import org.greenrobot.eventbus.EventBus

/**
 * create by zhaimi
 * description: eventbus 包装一下
 */
object BaseEventBusHelper {

    public val EVENT_BUS = EventBus.getDefault()

    fun post(event: Any) {
        EVENT_BUS.post(event)
    }

     fun register(subscriber: Any) {
        if (!EVENT_BUS.isRegistered(subscriber)) {
            EVENT_BUS.register(subscriber)
        }
    }

     fun unregister(subscriber: Any) {
        if (EVENT_BUS.isRegistered(subscriber)) {
            EVENT_BUS.unregister(subscriber)
        }
    }

     fun postSticky(event: Any) {
        EVENT_BUS.postSticky(event)
    }
}