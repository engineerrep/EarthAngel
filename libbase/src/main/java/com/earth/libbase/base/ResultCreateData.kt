package com.earth.libbase.base

import android.content.Context
import com.earth.angel.network.NetworkError
import com.tencent.imsdk.group.GroupInfo
import java.util.*

data class ResultCreateData(
    val code: Int,
    val msg: String,
    val data: GroupInfo
    )