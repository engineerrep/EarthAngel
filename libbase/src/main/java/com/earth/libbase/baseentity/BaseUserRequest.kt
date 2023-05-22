package com.earth.libbase.baseentity

import com.google.gson.annotations.SerializedName

data class BaseUserRequest(
    @SerializedName("userId")
    var userId: Long? = null
)
