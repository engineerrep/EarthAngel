package com.earth.libbase.entity

data class MessageListEntity(
    var createTime: String,
    var headImgUrl: String,
    var messageId: String,
    var msg: String,
    var nickName: String,
    var releaseRecordId: Long,
    var userId: Long
    )
