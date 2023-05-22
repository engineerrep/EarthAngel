package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class EmailCodeRequest(
    @SerializedName("code")
    var code: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("newPassword")
    var newPassword: String? = null
)
