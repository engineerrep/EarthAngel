package com.earth.libbase.entity

import java.io.Serializable

data class PointRecordEntity(
    var createTime: Long,
    var score: Int,
    var paymentType: String,
    var changeType: String
    ){
    companion object {
        const val SIGN_IN = "Sign"
        const val SHARE = "Share"
        const val READ = "Read"
        const val GIVE_AWAY = "Send"
        const val ASK_FOR = "Cost"
        const val RELEASE = "Post"
    }
}
