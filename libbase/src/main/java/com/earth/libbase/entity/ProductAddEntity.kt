package com.earth.libbase.entity

import java.io.Serializable

data class ProductAddEntity(
    var userId: String,
    var score: Int,
    var releaseDate: String,
    var whetherNeed: String,
    var releaseRecordId: String
    ){
    companion object{
        const val NEED="NEED"
        const val NONEED="NO_NEED"
    }
}
