package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class UnloadRequest (
    @SerializedName("id")
    var id: Long? = 0,
        )