package com.earth.libbase.entity

import java.io.Serializable

data class ArticleEntity(
    var uniqueCode: String,
    var title: String,
    var content: String,
    var announcer: String,
    var coverPicture: String,
    var createTime: Long


    ): Serializable
