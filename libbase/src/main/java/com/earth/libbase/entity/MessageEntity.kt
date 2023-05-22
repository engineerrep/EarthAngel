package com.earth.libbase.entity

import java.io.Serializable

data class MessageEntity (
    var contactDetails: String,
    var createTime: String,
    var headImgUrl: String,
    var id: String,
    var isConcern: Boolean,
    var messageStatus: Int,

    var msg: String,
    var nickName: String,
    var pictureResources: String,
    var releaseRecordId: Long,
    var userId: Int
        ): Serializable