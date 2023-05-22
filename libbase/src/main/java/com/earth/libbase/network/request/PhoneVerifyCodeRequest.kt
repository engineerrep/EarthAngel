package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class PhoneVerifyCodeRequest(
    @SerializedName("phoneCode")
    var phoneCode: String? = null,

    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,
)