package com.earth.libbase.baseentity

import com.earth.libbase.entity.ArticleEntity
import com.earth.libbase.entity.PicTureEntity
import com.earth.libbase.entity.RecordEntity

data class BasePageRecordEntity (
    var total: Int,
    var list:  MutableList<RecordEntity>,
    var pageNum: Int,
    var pageSize: Int,
    var endRow: Int

)