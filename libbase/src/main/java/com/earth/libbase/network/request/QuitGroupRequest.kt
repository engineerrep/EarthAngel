package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class QuitGroupRequest (
    @SerializedName("houseNumber")
    var houseNumber: Long? = 0,
    @SerializedName("houseName")
    var houseName: String? = null,
        )