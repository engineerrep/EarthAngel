package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class LikeUserRequest(

    @SerializedName("userId")
    var userId: Long? = 0
)
