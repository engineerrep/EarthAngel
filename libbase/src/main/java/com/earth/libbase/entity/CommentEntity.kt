package com.earth.libbase.entity

data class CommentEntity (
    var id: String,
    var createTime: String,
    var releaseRecordId: String,
    var releaseRecordUserId: String,
    var commentUserId: Long,
    var commentUserNickName: String,
    var commentUserHeadImgUrl: String,
    var commentUserMsg: String
        )