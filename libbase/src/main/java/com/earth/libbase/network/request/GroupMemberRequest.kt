package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class GroupMemberRequest(
    @SerializedName("houseNumber")
    var houseNumber: Long? = 0,
    @SerializedName("pageNum")
    var pageNum: Int? = 0,

    @SerializedName("pageSize")
    var pageSize: Int? = 10

)
