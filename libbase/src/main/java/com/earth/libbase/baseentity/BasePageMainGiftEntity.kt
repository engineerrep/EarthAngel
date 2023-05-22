package com.earth.libbase.baseentity

import com.earth.libbase.entity.PicTureEntity

data class BasePageMainGiftEntity (
    var newGifts:  MutableList<BaseGiftEntity>,
    var nearbyGifts: MutableList<BaseGiftEntity>,
    var bestEnvironmentalists: MutableList<BaseBestGiftEntity>
        )