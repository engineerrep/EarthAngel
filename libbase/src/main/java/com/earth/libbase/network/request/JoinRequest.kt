package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class JoinRequest (
    @SerializedName("houseNumber")
    var houseNumber: Long? = 0
    )