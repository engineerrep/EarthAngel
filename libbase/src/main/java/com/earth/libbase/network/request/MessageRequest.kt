package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class MessageRequest(
    @SerializedName("messageId")
    var messageId: String? = null,
    @SerializedName("description")
    var description: String? = null,
)
