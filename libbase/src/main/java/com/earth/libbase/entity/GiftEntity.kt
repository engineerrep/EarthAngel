package com.earth.libbase.entity

import java.io.Serializable

data class GiftEntity (
    var id: String,
    var createTime: String,
    var userId: String,
    var nickName: String,
    var headImgUrl: String,
    var description: String,
    var pictureResources: MutableList<PicTureEntity>,
    var releaseStatus: Int,
    var sendMode: Int,
    var newOrOld: Int,
    var releaseType: Int,
    var isExchange: Int,
    var releasedMessageRecords: MutableList<MessageEntity>,
    var isConcern: Boolean,
    var isLike: Boolean,
    var valuation: Double,
    var releasedCommentNumbers: Int,
    var releasedLikeNumbers: Int,
    var requestFrendsStatus: Boolean

    ) : Serializable