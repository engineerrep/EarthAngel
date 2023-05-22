package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class RegistRequest(
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("password")
    var password: String? = null,
    @SerializedName("invitationCode")
    var invitationCode: String? = null
)
