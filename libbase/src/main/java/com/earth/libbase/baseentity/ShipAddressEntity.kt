package com.earth.libbase.baseentity

import com.google.gson.annotations.SerializedName

data class ShipAddressEntity(
    var id: String,
    var district: String,
    var nickName: String,
    var phoneNumber: String,
    var province: String,
    var streetAddress: String,
    var zipCode: String,
)
