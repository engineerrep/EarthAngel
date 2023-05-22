package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class AddGiftRequest(
    @SerializedName("releaseRecordId")
    var releaseRecordId: Double? = null
)
