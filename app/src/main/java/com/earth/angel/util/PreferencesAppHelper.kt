package com.earth.angel.util

import android.content.Context
import com.earth.angel.appphoto.BasePhotoSaveEntity
import com.earth.angel.base.EarthAngelApp
import com.earth.libbase.util.getString
import com.earth.libbase.util.saveString
import com.google.gson.Gson


object PreferencesAppHelper {
    private const val LOCAL_POST = "localpost"
    private const val LOCAL_TEST = "test"


    fun savePostCache(context: Context, userInfoJson: String) {
        EarthAngelApp.mBasePhotoRequest = Gson().fromJson(userInfoJson, BasePhotoSaveEntity::class.java)
        context.saveString(PreferencesAppHelper.LOCAL_POST, userInfoJson)
    }
    fun fetchPostCache(context: Context): String? =
        context?.getString(PreferencesAppHelper.LOCAL_POST)

    fun saveTodayTime(context: Context,str: String) {
        context.saveString(LOCAL_TEST, str)
    }

    fun getTodayTime(context: Context): String? =
        context?.getString(LOCAL_TEST,"")
}