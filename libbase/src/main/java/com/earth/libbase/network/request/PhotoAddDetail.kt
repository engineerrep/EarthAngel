package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class PhotoAddDetail(
    @SerializedName("friendMobilePhoneName")
    var friendMobilePhoneName: String? = null,
    @SerializedName("friendMobilePhoneNumber")
    var friendMobilePhoneNumber: String? = null
)
