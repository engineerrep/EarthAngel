package com.earth.libbase.baseentity

import com.earth.libbase.entity.PicTureEntity

data class BaseGiftEntity (
    var createTime: String,
    var userId: String,
    var itemTitle: String,
    var description: String,
    var pictureResources: MutableList<String>,
    var mainPicture: String,
    var releaseStatus: Int,//DOING = 0,EXCHANGED = 1,REMOVE = 2,DELETE = 3
    var isLike: Boolean,
    var ownerId: String,
    var releaseRecordId: String,
    var isPoket: Boolean,
    var itemClassification: Int,
    var itemClassificationDescription: String,
    var itemCondition: String,
    var score: Int,


    ){
    companion object {
        const val DOING = 0
        const val EXCHANGED = 1
        const val REMOVE = 2
        const val DELETE = 3
    }
}