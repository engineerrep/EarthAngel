package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class CreatGroupRequest (
    @SerializedName("houseName")
    var houseName: String? = "",
    @SerializedName("houseLogo")
    var houseLogo: String? = "",
    @SerializedName("userIds")
    var userIds: List<Long>? = null,
    )