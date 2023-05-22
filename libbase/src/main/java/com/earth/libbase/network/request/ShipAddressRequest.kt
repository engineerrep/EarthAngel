package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class ShipAddressRequest(
    @SerializedName("district")
    var district: String? = null,
    @SerializedName("nickName")
    var nickName: String? = null,
    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,
    @SerializedName("province")
    var province: String? = null,
    @SerializedName("streetAddress")
    var streetAddress: String? = null,
    @SerializedName("zipCode")
    var zipCode: String? = null,
    @SerializedName("id")
    var id: String? = null
)
