package com.earth.libbase.entity

import java.io.Serializable

data class RecordEntity(
    var id: String,
    var createTime: Long,
    var toUserId: String,
    var toNickName: String,
    var score: Int,
    var nickName: String,
    var streetAddress: String,
    var district: String,
    var province: String,
    var zipCode: String,
    var phoneNumber: String,
    var itemTitle: String,
    var releaseId: String,
    var pictureResources: MutableList<String>
    ): Serializable
