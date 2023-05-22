package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class GroupNameRequest(
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("communityName")
    var communityName: String? = null,
    @SerializedName("latitude")
    var latitude: Double? = null,
    @SerializedName("longitude")
    var longitude: Double? = null
)
