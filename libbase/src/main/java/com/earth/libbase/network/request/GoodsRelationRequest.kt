package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class GoodsRelationRequest(
    @SerializedName("relationStatus")
    var relationStatus: Int? = null,

    @SerializedName("releaseRecordId")
    var releaseRecordId: Double? = null
)
