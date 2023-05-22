package com.earth.libbase.baseentity

import com.earth.libbase.entity.PicTureEntity

data class BasePageGiftEntity (
    var total: Int,
    var list:  MutableList<BaseGiftEntity>,
    var pageNum: Int,
    var pageSize: Int,
    var endRow: Int,
    var freeItems:  MutableList<BaseGiftEntity>,
    var offItems: MutableList<BaseGiftEntity>,
    var  headImgUrl: String,
    var  nickName: String,
    var  liveIn: String,
    var  fansNumber: String,
    var  successfulReplacementNumber: String,
    var  isConcern: Boolean
    )