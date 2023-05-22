package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class UserUpdateRequest(
    @SerializedName("firstName")
    var firstName: String? = null,
    @SerializedName("headImgUrl")
    var headImgUrl: String? = null,
    @SerializedName("lastName")
    var lastName: String? = null,
    @SerializedName("latitude")
    var latitude: Double? = null,
    @SerializedName("liveIn")
    var liveIn: String? = null,
    @SerializedName("longitude")
    var longitude: Double? = null
)
