package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class CommentAddRequest(
    @SerializedName("commentUserMsg")
    var commentUserMsg: String? = null,

    @SerializedName("releaseRecordId")
    var releaseRecordId: Long? = null
)
