package com.earth.libbase.base

import android.annotation.SuppressLint
import android.content.Context
import com.earth.libbase.entity.FilterEntity
import com.earth.libbase.entity.MineEntity
import com.google.gson.Gson
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * Create by liwen on 2020/6/4
 */
class BaseContext private constructor() {


    private lateinit var mContext: Context

    fun init(context: Context) {
        mContext = context
    }

    fun getContext(): Context {
        return mContext
    }

    companion object {

        val instance = Singleton.holder

        object Singleton {
            val holder = BaseContext()
        }

    }

}