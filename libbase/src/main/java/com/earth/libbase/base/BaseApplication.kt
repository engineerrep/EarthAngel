package com.earth.libbase.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.earth.libbase.baseentity.BaseUser
import com.earth.libbase.entity.MineEntity
import com.earth.libbase.network.baserequest.BasePhotoRequest
import com.earth.libbase.util.PreferencesHelper
import com.google.gson.Gson

open class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        instance = applicationContext
        myBaseUser = PreferencesHelper.fetchMyProfileCache(this).run {
            Gson().fromJson(this, BaseUser::class.java)
        }


    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: Context
        var myBaseUser: BaseUser? = null

    }

}