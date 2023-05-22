package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    @SerializedName("headImgUrl")
    var headImgUrl: String? = null,
    @SerializedName("nickName")
    var nickName: String? = null

)
