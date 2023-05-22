package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class CommentRequest(
    @SerializedName("pageNum")
    var pageNum: Int? = 0,

    @SerializedName("pageSize")
    var pageSize: Int? = 10,
    @SerializedName("id")
    var id: Long? = null,
    @SerializedName("releaseRecordId")
    var releaseRecordId: Long? = null,
    @SerializedName("userId")
    var userId: Long? = null,
    @SerializedName("itemCode")
    var itemCode: Int? = null,
    @SerializedName("uniqueCode")
    var uniqueCode: String? = null,
    @SerializedName("tradingType")
    var tradingType: String? = null,
    @SerializedName("latitude")
    var latitude: Double? = null,
    @SerializedName("longitude")
    var longitude: Double? = null
)
