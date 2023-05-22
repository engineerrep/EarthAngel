package com.earth.libbase.entity

data class FriendRequestUserEntity (
    var id: String,
    var createTime: String,
    var frendUserId: String,
    var frendUserNickName: String,
    var frendUserHeadImgUrl: String,
    var frendType: Long,
    var letters: String,
    var select: Boolean=false
        )