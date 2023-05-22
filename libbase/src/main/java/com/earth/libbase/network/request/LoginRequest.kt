package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("phoneCode")
    var phoneCode: String? = null,

    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,
    @SerializedName("sharerHouseNumber")
    var sharerHouseNumber: Int? = null,
    @SerializedName("sharerUserId")
    var sharerUserId: Long? = null
)
