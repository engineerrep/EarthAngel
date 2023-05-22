package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class InviteGroupRequest (

    @SerializedName("houseNumber")
    var houseNumber: Long? = null,
    @SerializedName("userIds")
    var userIds: List<Long>? = null,
    )