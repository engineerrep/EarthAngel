package com.earth.libbase.baseentity

import com.earth.libbase.entity.ArticleEntity
import com.earth.libbase.entity.GroupEntity
import com.earth.libbase.entity.PicTureEntity

data class BasePageGroupEntity (
    var total: Int,
    var list:  MutableList<GroupEntity>,
    var pageNum: Int,
    var pageSize: Int
)