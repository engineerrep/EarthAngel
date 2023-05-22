package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class PageGiftListRequest(

    @SerializedName("houseNumber")
    var houseNumber: Long? = 0,

    @SerializedName("pageNum")
    var pageNum: Int? = 0,

    @SerializedName("pageSize")
    var pageSize: Int? = 10,

    @SerializedName("isSelf")
    var isSelf: Boolean? = null,

    @SerializedName("releaseTypes")
    var releaseTypes: List<Int>? = null,

    @SerializedName("sortField")
    var sortField: String? = null,

    @SerializedName("sortType")
    var sortType: Int? = null

)
