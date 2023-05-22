package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class FriendRequest(
    @SerializedName("frendUserId")
    var frendUserId: Long? = null
)
