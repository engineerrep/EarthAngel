package com.earth.libbase.entity

import java.io.Serializable

data class UserLocationEntity (
    var id: String,
    var createTime: String,
    var nickName: String,
    var headImgUrl: String,
    var releasesNumber: String,
    var successfulReplacementNumber: String,
    var fansNumber: Long,
    var concernNumber: Long,
    var phoneCode: String,
    var phoneNumber: String,
    var isConcern: Boolean,
    var requestFrendsStatus: Boolean,
    var distance: Int
        ): Serializable