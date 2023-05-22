package com.earth.libbase.baseentity

import com.earth.libbase.entity.PicTureEntity

data class BasePagePocketEntity (
    var total: Int,
    var list:  MutableList<BasePocketEntity>,
    var pageNum: Int,
    var pageSize: Int
        )