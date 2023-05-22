package com.earth.libbase.entity

import java.io.Serializable

data class UserEntity (
    var id: String,
    var createTime: String,
    var nickName: String,
    var headImgUrl: String,
    var releasesNumber: String,
    var fansNumber: Long,
    var concernNumber: Long,
    var isConcern: Boolean,
    var requestFrendsStatus: Boolean,
    var successfulReplacementNumber: Boolean,
    ): Serializable