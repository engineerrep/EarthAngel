package com.earth.libbase.entity

import java.io.Serializable

data class MemberEntity (
    var houseName: String,
    var houseNumber: Long,
    var destructionTime: Long,
    var userId: Long,
    var nickName: String,
    var headImgUrl: String,
    var isFounder: Boolean,
    var isConcern: Boolean,
    var requestFrendsStatus: Boolean
) : Serializable