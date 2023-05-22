package com.earth.libbase.network.request

import com.google.gson.annotations.SerializedName

data class SendGiftRequest(


    @SerializedName("exchangePersonId")//交换物品的用户id
    var exchangePersonId: Long? = null,
    @SerializedName("id")//旧物记录id
var id: Long? = null
)
