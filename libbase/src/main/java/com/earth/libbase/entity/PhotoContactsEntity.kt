package com.earth.libbase.entity

data class PhotoContactsEntity(
    var id: String?=null,
    var ownerUserId: String?=null,
    var friendMobilePhoneNumber: String?=null,
    var friendMobilePhoneName: String?=null,
    var friendUserId: String?=null,
    var nickName: String?=null,
    var headImgUrl: String?=null,
    var isConcern: Boolean?=null,
    var requestFrendsStatus: Boolean?=null,

    )
