package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class BaseFileRequest(
    @SerializedName("base64")
    var base64: String? = null
)
