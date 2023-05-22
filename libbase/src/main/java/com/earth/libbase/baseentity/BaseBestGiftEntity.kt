package com.earth.libbase.baseentity

import com.earth.libbase.entity.PicTureEntity

data class BaseBestGiftEntity (
    var userId: String,
    var nickName: String,
    var headImgUrl: String,
    var liveIn: String,
    var isConcern: Boolean,
    var items: MutableList<BaseGiftEntity>
        )