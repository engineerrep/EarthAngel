package com.earth.libbase.baseentity

import com.earth.libbase.entity.PicTureEntity

data class BasePocketEntity (
    var firstName: String,
    var lastName: String,
    var nickName: String,
    var headImgUrl: String,
    var userId: String,

    var poketList: MutableList<BaseGiftEntity>

    )