package com.earth.libbase.entity

import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

data class HouseListEntity (
    var type: Int,
    var name: String,
    var imgUrl: List<String>,
    var number: String,
    var members: Long,
    var releaseTime: Long,
    var releasesNumber: Long,
    var message: Int=0,
    var requestFrendsStatus: Boolean,
    var lastMesssage: String,
    var messageTime: Long=0
): Serializable , Comparable<HouseListEntity> {
    override fun compareTo(other: HouseListEntity): Int {
        val messageTime1=messageTime
        val messageTime2=other.messageTime
        var  sum=messageTime2 -messageTime1
        return sum.toInt()
    }
}