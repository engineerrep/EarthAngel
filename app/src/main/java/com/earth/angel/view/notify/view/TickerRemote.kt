package com.earth.angel.view.notify.view

import com.earth.angel.view.notify.utils.ContextUtil

open class TickerRemote(var  tickerText: CharSequence?="", layoutId: Int) :
    AbRemoteViews(ContextUtil.getApplication().packageName, layoutId) {


}