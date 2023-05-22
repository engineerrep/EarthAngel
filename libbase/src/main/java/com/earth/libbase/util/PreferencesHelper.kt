package com.earth.libbase.util

import android.content.Context
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.baseentity.BaseUser
import com.earth.libbase.entity.FilterEntity
import com.earth.libbase.entity.MineEntity
import com.earth.libbase.network.baserequest.BasePhotoRequest

import com.google.gson.Gson


object PreferencesHelper {
    private const val LOCAL_POST = "localpost"
    private const val LOCAL_BASE_MY_PROFILE_DATA = "myprofile"
    private const val LOCATION_DATA = "firstLocation"
    private const val NOCATION_DATA = "nocation"

    private const val WELCOME_DATA = "welcome"
    private const val FIRSTART_DATA = "firstArt"
    private const val FIRST_DATA = "firstAuth"
    private const val TOKEN_DATA = "token"
    private const val IM_TOKEN_DATA = "imtoken"
    private const val NAME_DATA = "name"
    private const val EMAIL_DATA = "email"

    private const val PHONE_DATA = "phone"
    private const val HEAD_DATA = "head"
    private const val CONTACTS = "contacts"
    private const val LOCATION = "location"

    private const val ID_DATA = "id"
    private const val MESSAGE_NAME_DATA = "message_name"
    private const val MESSAGE_INFO_DATA = "message_info"
    private const val TITLE_DATA = "title_position"
    private const val JSON_DATA = "json"
    private const val FILTER = "filter"
    private const val AGREEMENT = "agreement"
    private const val TIME = "time" // 每天时间
    private const val MESSAGE = "message" // 消息
    fun saveWelcome(context: Context, flag: Boolean) {
        context.saveBoolean(WELCOME_DATA, flag)
    }
    fun getWelcome(context: Context): Boolean? =
        context?.getBoolean(WELCOME_DATA,false)

    fun saveFirstArt(context: Context, flag: Boolean) {
        context.saveBoolean(FIRSTART_DATA, flag)
    }

    fun getFirstArt(context: Context): Boolean? =
        context?.getBoolean(FIRSTART_DATA,false)


    fun saveMessage(context: Context, message: String) {
        context.saveString(MESSAGE, message)
    }

    fun getMessage(context: Context): String? =
        context?.getString(MESSAGE,"")

    fun saveTodayTime(context: Context) {
        context.saveString(TIME, "")
    }

    fun getTodayTime(context: Context): String? =
        context?.getString(TIME,"")

    fun saveAgreement(context: Context, flag: Boolean) {
        context.saveBoolean(AGREEMENT, flag)
    }

    fun getAgreement(context: Context): Boolean? =
        context?.getBoolean(AGREEMENT,false)

    fun saveFilter(context: Context, filter: String) {
      //  BaseApplication.mFilterEntity = Gson().fromJson(filter, FilterEntity::class.java)
        context.saveString(FILTER, filter)
    }

    fun getFilterCache(context: Context): String? =
        context?.getString(FILTER)

    fun saveLocation(context: Context, flag: Boolean) {
        context.saveBoolean(LOCATION_DATA, flag)
    }

    fun getLocation(context: Context): Boolean? =
        context?.getBoolean(LOCATION_DATA,false)


    fun saveMyProfileCache(context: Context, userInfoJson: String) {
        BaseApplication.myBaseUser = Gson().fromJson(userInfoJson, BaseUser::class.java)
        context.saveString(LOCAL_BASE_MY_PROFILE_DATA, userInfoJson)
    }
    fun fetchMyProfileCache(context: Context): String? =
        context?.getString(LOCAL_BASE_MY_PROFILE_DATA)



    fun saveContacts(context: Context, flag: Boolean) {
        context.saveBoolean(CONTACTS, flag)
    }

    fun getContacts(context: Context): Boolean? =
        context?.getBoolean(CONTACTS)

    fun saveHead(context: Context, head: String) {
        context.saveString(HEAD_DATA, head)
    }

    fun getHead(context: Context): String? =
        context?.getString(HEAD_DATA, "")

    fun saveImtoken(context: Context, imtoken: String) {
        context.saveString(IM_TOKEN_DATA, imtoken)
    }

    fun getImtoken(context: Context): String? =
        context?.getString(IM_TOKEN_DATA, "")

    fun saveJson(context: Context, json: String) {
        context.saveString(JSON_DATA, json)
    }

    fun getJson(context: Context): String? =
        context?.getString(JSON_DATA, "")

    fun saveTitle(context: Context, TITLE: Int) {
        context.saveInteger(TITLE_DATA, TITLE)
    }

    fun getTitle(context: Context): Int? =
        context?.getInteger(TITLE_DATA, 0)

    fun saveToken(context: Context, token: String) {
        context.saveString(TOKEN_DATA, token)
    }

    fun getToken(context: Context): String? =
        context?.getString(TOKEN_DATA, "")

    fun saveFirstAuth(context: Context, token: Boolean) {
        context.saveBoolean(FIRST_DATA, token)
    }

    fun getFirstAuth(context: Context): Boolean? =
        context?.getBoolean(FIRST_DATA, true)

    fun saveName(context: Context, name: String) {
        context.saveString(NAME_DATA, name)
    }

    fun getName(context: Context): String? =
        context?.getString(NAME_DATA, "")

    fun saveEmail(context: Context, name: String) {
        context.saveString(EMAIL_DATA, name)
    }

    fun getEmail(context: Context): String? =
        context?.getString(EMAIL_DATA, "")

    fun savePhone(context: Context, phone: String) {
        context.saveString(PHONE_DATA, phone)
    }

    fun getPhone(context: Context): String? =
        context?.getString(PHONE_DATA, "")

    fun saveUserId(context: Context, userid: String) {
        context.saveString(ID_DATA, userid)
    }

    fun getUserId(context: Context): String? =
        context?.getString(ID_DATA, "")


    fun saveMessageName(context: Context, name: String) {
        context.saveString(MESSAGE_NAME_DATA, name)
    }

    fun getMessageName(context: Context): String? =
        context?.getString(MESSAGE_NAME_DATA, "")

    fun saveMessageInfo(context: Context, info: String) {
        context.saveString(MESSAGE_INFO_DATA, info)
    }

    fun getMessageInfo(context: Context): String? =
        context?.getString(MESSAGE_INFO_DATA, "")

    fun saveLocationName(context: Context, name: String) {
        context.saveString(LOCATION, name)
    }

    fun getLocationName(context: Context): String? =
        context?.getString(LOCATION, "")


    fun saveNocation(context: Context, flag: Boolean) {
        context.saveBoolean(NOCATION_DATA, flag)
    }

    fun getNocation(context: Context): Boolean? =
        context?.getBoolean(NOCATION_DATA,false)

    fun clear(context: Context) {
        saveNocation(context,false)
        saveMessage(context,"")
        saveFilter(context,"")
        saveLocation(context, false)
        saveMyProfileCache(context,"")
        saveContacts(context, false)
        saveHead(context, "")
        saveImtoken(context, "")
        saveFirstAuth(context,false)
        saveJson(context, "")
        saveTitle(context, 0)
        saveToken(context, "")
        saveName(context, "")
        savePhone(context, "")
        saveUserId(context, "")
        saveMessageName(context, "")
        saveMessageInfo(context, "")
        saveLocationName(context, "")
    }
}