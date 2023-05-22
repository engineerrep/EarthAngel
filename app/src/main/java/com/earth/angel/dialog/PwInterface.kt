package com.earth.angel.dialog

interface PwInterface {
    open fun onItemClick(position: Int)
    fun onBankString(str: String)
    fun onDismiss()
    fun onEmpty()
}