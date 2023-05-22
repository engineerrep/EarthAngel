package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class CommonRequest(
    @SerializedName("pageNum")
    var pageNum: Int? = null,

    @SerializedName("pageSize")
    var pageSize: Int? = null,

    @SerializedName("userId")
    var userId: Long? = null,

    @SerializedName("isExchange")
    var isExchange: Int? = null,

    @SerializedName("latitude")
    var latitude: Double? = null,

    @SerializedName("longitude")
    var longitude: Double? = null,

    @SerializedName("releaseStatus")
    var releaseStatus: Int? = null,
){
    companion object {
        const val DOING = 0
        const val EXCHANGED = 1
        const val REMOVE = 2
        const val DELETE = 3
    }
}
