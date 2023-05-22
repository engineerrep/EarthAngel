package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class UpdateMobileNumberRequest(
    @SerializedName("code")
    var code: String? = null,

    @SerializedName("phoneCode")
    var phoneCode: String? = null,
    @SerializedName("phoneNumber")
    var phoneNumber: String? = null
)