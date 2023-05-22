package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class GroupRequest(
    @SerializedName("communityName")
    var communityName: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("picUrl")
    var picUrl: String? = null,
    @SerializedName("communityId")
    var communityId: String? = null
)
