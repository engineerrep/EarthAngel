package com.earth.libbase.entity

import java.io.Serializable

data class SingEntity(
    var id: Long,
    var createTime: Long,
    var userId: Long,
    var signInCount: Int,
    var score: Int,
    var signInTime: String,
    var whetherNeed: String
) : Serializable {
    companion object {
        const val NEED = "NEED"
        const val NONEED = "NO_NEED"
    }
}
