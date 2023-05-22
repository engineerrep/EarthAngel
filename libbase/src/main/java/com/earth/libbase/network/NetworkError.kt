package com.earth.angel.network

import android.content.Context
import android.content.Intent
import android.widget.Switch
import androidx.core.content.ContextCompat.startActivity

import org.jetbrains.anko.toast

object NetworkError {
    fun  error(context: Context?,code: Int,msg: String?){
        when(code){
            501 ->{
            /*    V2TIMManager.getInstance().logout(object : V2TIMCallback {
                    override fun onSuccess() {
                    }
                    override fun onError(code: Int, desc: String?) {
                    }
                })
                ActivityStackManager.finishAll()
                context?.let {
                    PreferencesHelper.clear(EarthAngelApp.instance)
                    it.startActivity(Intent(it, LoginActivity::class.java))
                }*/
            }
            else -> {
                context?.let {
                    msg?.let { msg ->
                        it.toast(msg)
                    }
                }
            }
        }
    }
}