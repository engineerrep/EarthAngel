package com.earth.libbase.baseentity

import com.earth.libbase.entity.PicTureEntity

data class BaseChatPageGiftEntity (

    var poketList:  MutableList<BaseGiftEntity>,
    var  enough: String
    ){
    companion object{
        const val NOTENOUGH="NOT_ENOUGH"
        const val ENOUGH="ENOUGH"

    }
}