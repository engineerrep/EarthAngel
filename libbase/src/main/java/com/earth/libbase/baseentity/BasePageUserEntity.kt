package com.earth.libbase.baseentity

import com.earth.libbase.entity.PicTureEntity

data class BasePageUserEntity (
    var total: Int,
    var list:  MutableList<BaseUser>,
    var pageNum: Int,
    var pageSize: Int,
    var endRow: Int

)