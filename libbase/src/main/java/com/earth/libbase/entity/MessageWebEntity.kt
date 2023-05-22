package com.earth.libbase.entity

data class MessageWebEntity(
    var text: String,
    var customCellType: String,
    var data: WebUserInfoEntity
)