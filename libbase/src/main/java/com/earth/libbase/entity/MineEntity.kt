package com.earth.libbase.entity

import java.io.Serializable

data class MineEntity (
    var id: String,
    var createTime: String,
    var nickName: String,
    var headImgUrl: String,
    var releasesNumber: String,
    var fansNumber: Long,
    var phoneCode: String,
    var phoneNumber: String,
    var concernNumber: Long,
    var isConcern: Boolean,
    var distance: Long,

    ): Serializable