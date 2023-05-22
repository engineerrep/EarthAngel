package com.earth.libbase.network

import com.google.gson.annotations.SerializedName

data class ReleasedMessageRequest(
    @SerializedName("contactDetails")
    var contactDetails: String? = "",
    @SerializedName("msg")
    var msg: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("releaseRecordId")
    var releaseRecordId: Long? = 0
)
