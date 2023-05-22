package com.earth.libbase.baseentity

import com.earth.libbase.entity.MessageListEntity
import com.earth.libbase.entity.PicTureEntity
import com.earth.libbase.entity.PointRecordEntity

data class BasePageListPointEntity (
    var total: Int,
    var list:  MutableList<PointRecordEntity>,
    var pageNum: Int,
    var pageSize: Int,
    var endRow: Int
    )