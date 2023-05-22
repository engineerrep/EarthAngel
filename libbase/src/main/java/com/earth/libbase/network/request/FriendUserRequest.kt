package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class FriendUserRequest(
    @SerializedName("id")
    var id: Long? = 0
)
