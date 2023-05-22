package com.earth.libbase.entity

import java.io.Serializable

data class GiftHouseEntity (
     var founderId: String,
     var createTime: String,
     var founderNickName: String,
     var founderHeadImgUrl: String,
     var houseName: String,
     var houseNumber: Long,
     var houseLogo: String,
     var destructionTime: Int,
     var isFounder: Boolean,
     var members: Int,
         ) : Serializable