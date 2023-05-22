package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class UserRequest(

    @SerializedName("userId")
    var userId: Long? = 0,
    @SerializedName("id")
    var id: Int? = 0
)
