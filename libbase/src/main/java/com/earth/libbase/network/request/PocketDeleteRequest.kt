package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class PocketDeleteRequest(
    @SerializedName("releaseRecordIds")
    var releaseRecordIds: List<Long>? = null,
    @SerializedName("ownerId")
    var ownerId: Long? = null
)
