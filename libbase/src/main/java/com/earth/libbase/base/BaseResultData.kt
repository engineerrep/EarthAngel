package com.earth.libbase.base

import android.content.Context
import com.earth.angel.network.NetworkError

data class BaseResultData<T>(
    val data: T?,
    val code: Int,
    val msg: String,
    val action: String,
    val pages: Int

    ){

    fun isOk(context: Context?): Boolean {
        return if (code == 200) {
            true
        }else{
            NetworkError.error(context,code,msg)
            false
        }
    }

}