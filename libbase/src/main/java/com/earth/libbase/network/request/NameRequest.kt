package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class NameRequest(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("link")
    var link: String? = null,
)
