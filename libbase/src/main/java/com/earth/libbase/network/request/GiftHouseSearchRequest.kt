package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class GiftHouseSearchRequest(

    @SerializedName("searchKey")
    var searchKey: String? = null,
    @SerializedName("searchType")
    var searchType: Int? = null,
    @SerializedName("pageNum")
    var pageNum: Int? = 0,
    @SerializedName("pageSize")
    var pageSize: Int? = 10,
    @SerializedName("userId")
    var userId: Long? = 0

)
