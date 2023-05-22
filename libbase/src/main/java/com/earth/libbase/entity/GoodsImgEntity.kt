package com.earth.libbase.entity

data class GoodsImgEntity (
    var code: Int,
    var data: GoodsImgUrlEntity,
    var message: String,
    var request_id: String,
    var status: Int,
    var time_elapsed: String
        )