package com.earth.libbase.baseentity

import com.earth.libbase.entity.MessageListEntity
import com.earth.libbase.entity.PicTureEntity

data class BasePageListMessageEntity (
    var total: Int,
    var list:  MutableList<MessageListEntity>,
    var pageNum: Int,
    var pageSize: Int,
    var endRow: Int
    )