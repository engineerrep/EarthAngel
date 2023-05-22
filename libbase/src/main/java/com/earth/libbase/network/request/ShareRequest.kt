package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class ShareRequest(
    @SerializedName("userId")
    var userId: Long? = null,
    @SerializedName("id")
    var id: Long? = null,
    @SerializedName("textContent")
    var textContent: String? = null,
    @SerializedName("textTitle")
    var textTitle: String? = null
)