package com.earth.libbase.baseentity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.databinding.BaseObservable

/**
 * Create by liwen on 2020/5/27
 */
data class BaseUser(
    val userId: String?,
    val createTime: String?,
    val nickName: String? ,
    val fansNumber: Int=0 ,
    val concernNumber: Int=0 ,
    val isConcern: Boolean?=null ,
    val firstName: String?,
    val headImgUrl: String?,
    val lastName: String?,
    val latitude: Double = 0.0,
    val liveIn: String?,
    val longitude: Double =0.0,
    val successfulReplacementNumber: Int=0,
    val postNumber: String,
    val poketNumber: String,
    val score: Int,

    )
