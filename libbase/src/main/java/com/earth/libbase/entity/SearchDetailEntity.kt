package com.earth.libbase.entity

import java.io.Serializable

data class SearchDetailEntity (
    var imgUrl : String,
    var name : String,
    var number : String,
    var status : Boolean,
    var requestFrendsStatus: Boolean
): Serializable