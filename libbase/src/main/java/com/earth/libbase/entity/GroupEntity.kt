package com.earth.libbase.entity

import java.io.Serializable

data class GroupEntity(
    var id: String,
    var createTime: Long,
    var userId: String,
    var communityName: String,
    var picUrl: String,
    var itemNum: Int,
    var userNum: Int,
    var isCreator: Boolean,
    var nickName: String,
    var description: String,
    var longitude: Double,
    var latitude: Double,
    var address: String,
    var editDescription: String,
    var imageUpload: String,
    var releaseGoods: String,
    var joinCommunity: String,
    var isJoined: Boolean,
    var isSelect: Boolean=false,
    var headImgUrl: String,

    ): Serializable
