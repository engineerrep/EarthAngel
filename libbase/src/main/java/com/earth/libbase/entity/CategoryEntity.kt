package com.earth.libbase.entity

import java.io.Serializable

data class CategoryEntity(
var itemCode: Int?=0,
var description: String?="",
var coverPicture: String?="",
var choose: Boolean?=false
) : Serializable
