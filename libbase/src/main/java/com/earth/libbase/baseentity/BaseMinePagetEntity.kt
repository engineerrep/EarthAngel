package com.earth.libbase.baseentity

import com.earth.libbase.entity.PicTureEntity

data class BaseMinePagetEntity (
    var  userId: String,
    var  createTime: String,
    var  firstName: String,
    var  lastName: String,
    var  nickName: String,
    var  headImgUrl: String,
    var  liveIn: String,
    var  fansNumber: String,
    var  concernNumber: String,
    var  poketNumber: String,
    var  postNumber: String,
    var  possessionsNumber: String,
    var  isConcern: Boolean,
    var  successfulReplacementNumber: String,
    var freeItems:  MutableList<BaseGiftEntity>,
    var offItems: MutableList<BaseGiftEntity>
        )