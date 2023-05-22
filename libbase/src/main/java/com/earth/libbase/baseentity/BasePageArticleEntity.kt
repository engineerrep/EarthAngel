package com.earth.libbase.baseentity

import com.earth.libbase.entity.ArticleEntity
import com.earth.libbase.entity.PicTureEntity

data class BasePageArticleEntity (
    var total: Int,
    var list:  MutableList<ArticleEntity>,
    var pageNum: Int,
    var pageSize: Int,
    var endRow: Int

)