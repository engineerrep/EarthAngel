package com.earth.libbase.entity

import com.earth.libbase.baseentity.BaseUser

data class LoginEntity (
    var uid: Long,
    var firstAuth: Boolean,
    var token: String,
    var imSig: String,
    var user: BaseUser
    )