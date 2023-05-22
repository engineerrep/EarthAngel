package com.earth.libbase.base

import java.util.*

class PremissionObservable : Observable() {


    fun newMessage(msg: String?) {
        setChanged()
        notifyObservers(msg)
    }

    companion object {
        var messageObservable = PremissionObservable()
    }
}