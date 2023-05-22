package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class ReportRequest(
    @SerializedName("description")
    var description: String? = "",
    @SerializedName("releaseRecordId")
    var releaseRecordId: Long? = 0)
