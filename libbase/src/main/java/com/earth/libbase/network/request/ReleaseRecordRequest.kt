package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class ReleaseRecordRequest (
    @SerializedName("id")
    var id: Long? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("releaseType")
    var releaseType: Int? = null,
    @SerializedName("newOrOld")
    var newOrOld: Int? = null,
    @SerializedName("sendMode")
    var sendMode: Int? = 0,
    @SerializedName("pictureResources")
    var pictureResources: List<PictureUrlRequest>?,
    @SerializedName("valuation")
    var valuation: Double? = null

)