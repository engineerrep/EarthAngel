package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class PictureUrlRequest (
    @SerializedName("pictureUrl")
    var pictureUrl: String? = ""
        )