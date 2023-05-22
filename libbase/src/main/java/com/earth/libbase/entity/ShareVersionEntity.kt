package com.earth.libbase.entity

data class ShareVersionEntity (
    var id: String,
    var createTime: Long,
    var userId: Long,
    var version: Int,
    var textContent: String,
        )