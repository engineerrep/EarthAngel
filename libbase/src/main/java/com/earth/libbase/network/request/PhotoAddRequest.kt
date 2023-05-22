package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class PhotoAddRequest(
    @SerializedName("insertDTOList")
    var insertDTOList: List<PhotoAddDetail>?= null
)
