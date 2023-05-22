package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("newPassword")
    var newPassword: String? = null,
    @SerializedName("oldPassword")
    var oldPassword: String? = null
)
