package com.earth.libbase.base

import java.util.*

class MessageObservable : Observable() {


    fun newMessage(msg: UpdateEntity?) {
        setChanged()
        notifyObservers(msg)
    }

    companion object {
        var messageObservable = MessageObservable()
    }
}