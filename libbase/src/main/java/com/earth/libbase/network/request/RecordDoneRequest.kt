package com.earth.libbase.network.request

import com.earth.libbase.baseentity.BaseGiftEntity
import com.google.gson.annotations.SerializedName

data class RecordDoneRequest(
    @SerializedName("exchangePersonId")
    var exchangePersonId: Long? = null,
    @SerializedName("ids")
    var ids: List<String>? = null,
    @SerializedName("nickName")
    var nickName: String? = null,
    @SerializedName("district")
    var district: String? = null,
    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,
    @SerializedName("province")
    var province: String? = null,
    @SerializedName("streetAddress")
    var streetAddress: String? = null,
    @SerializedName("zipCode")
    var zipCode: String? = null,
)
