package com.earth.libbase.base

data class UpdateEntity(
    var headUrl: String? = null,
    var pockNum: String? = null,
    var nikeName: String? = null,
    var postProduct: String? = null,
    var messageNum: String? = null,
    var messagePoint: String? = null, //是否更新环保分值
    var permission: String? = null,
    var groupList: String? = null //社群是否更新

){
    companion object{
        const val POINT = "POINT"
        const val GROUP= "GROUP"

    }
}