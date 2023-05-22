package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class LocationRequest(
    @SerializedName("pageNum")
    var pageNum: Int? = 0,
    @SerializedName("pageSize")
    var pageSize: Int? = 10,
    @SerializedName("latitude")
    var latitude: Double? = null,
    @SerializedName("longitude")
    var longitude: Double? = null
)
