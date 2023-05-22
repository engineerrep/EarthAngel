package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class PasswordEmailRequest(
    @SerializedName("email")
    var email: String? = null
)
