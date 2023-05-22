package com.earth.libbase.entity

data class GiftWantMessageEntity (
    var id: String,
    var createTime: String,
    var msg: String,
    var contactDetails: String,
    var userId: Int,
    var nickName: String,
    var headImgUrl: String,
    var pictureResources: String,
    var releaseRecordId: Long,
    var messageStatus: Int
        )