package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class GoodRelationRequest(
    @SerializedName("relationStatus")
    var relationStatus: Int? = 0,
    @SerializedName("releaseRecordId")
    var releaseRecordId: Long? = 0

)
